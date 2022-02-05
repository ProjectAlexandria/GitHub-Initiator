package net.kikkirej.alexandria.initiator.github

import net.kikkirej.alexandria.initiator.github.camunda.CamundaLayer
import net.kikkirej.alexandria.initiator.github.config.GeneralProperties
import net.kikkirej.alexandria.initiator.github.config.GitHubInitConfig
import net.kikkirej.alexandria.initiator.github.config.GitHubSourceConfig
import net.kikkirej.alexandria.initiator.github.db.*
import net.kikkirej.alexandria.initiator.github.github.GitCloneService
import net.kikkirej.alexandria.initiator.github.github.GitHubFacade
import org.kohsuke.github.GHBranch
import org.kohsuke.github.GHRepository
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import java.io.File

@Component
class GitHubInitiatorLogic(
    @Autowired val gitHubFacade: GitHubFacade,
    @Autowired val gitHubInitConfig: GitHubInitConfig,
    @Autowired val sourceRepository: SourceRepository,
    @Autowired val projectRepository: ProjectRepository,
    @Autowired val versionRepository: VersionRepository,
    @Autowired val analysisRepository: AnalysisRepository,
    @Autowired val generalProperties: GeneralProperties,
    @Autowired val camundaLayer: CamundaLayer,
    @Autowired val gitCloneService: GitCloneService,
) {

    private val log = LoggerFactory.getLogger(javaClass)

    @Scheduled(initialDelay = 100, fixedRate = 24*60*60*1000)
    fun run() {
        for (source in gitHubInitConfig.sources){
            val dbSource: Source = getSourceDB(source)
            val analyzingObjects = gitHubFacade.getAnalyzingObjectsForSource(source)
            handleAnalysisOf(analyzingObjects, dbSource, source)
        }
    }

    private fun getSourceDB(sourceConfig: GitHubSourceConfig): Source {
        val sourceOptional = sourceRepository.findById(sourceConfig.id)
        if(sourceOptional.isPresent){
            if(sourceOptional.get().type != "GitHub"){
                throw RuntimeException("")
            }
            return sourceOptional.get()
        }
        val source = Source(id = sourceConfig.id, name = sourceConfig.name,)
        sourceRepository.save(source)
        return source
    }

    private fun handleAnalysisOf(
        analyzingObjects: Map<GHRepository, List<GHBranch>>,
        source: Source,
        sourceConfig: GitHubSourceConfig
    ) {
        for(repository in analyzingObjects.keys){
            log.info("Starting analysis for Repository $repository")
            val dbProject = getDBProject(repository, source)
            val branches = analyzingObjects[repository]
            for(branch in branches!!){
                log.info("Starting analysis for Branch $branch")
                val version = getDBVersion(branch, dbProject, repository.defaultBranch == branch.name)
                val analysis = Analysis(version = version)
                analysisRepository.save(analysis)
                val filePath = getFilePath(analysis)
                gitCloneService.clone(filePath, repository.httpTransportUrl, version.name, sourceConfig)
                camundaLayer.startProcess(project = dbProject, version= version, analysis= analysis, filePath)
            }
        }
    }

    private fun getFilePath(analysis: Analysis): String {
        val upperFolder = File(generalProperties.sharedfolder);
        val analysisFolder = File(upperFolder.absolutePath + File.separator + analysis.id)
        return analysisFolder.absolutePath
    }

    private fun getDBVersion(branch: GHBranch, dbProject: Project, default: Boolean) : Version{
        val versionOptional = versionRepository.findByProjectAndName(dbProject, branch.name)
        val version: Version
        if(versionOptional.isPresent){
            version = versionOptional.get()
            version.default_version=default
        }else{
            version = Version(name = branch.name, project = dbProject, default_version = default)
        }
        version.setMetadata("protected", branch.isProtected)
        version.setMetadata("shA1", branch.shA1)
        return version
    }

    private fun getDBProject(repository: GHRepository, source: Source): Project {
        val projectOptional = projectRepository.findByExternalIdentifierAndSource(repository.id.toString(), source)
        val project: Project
        if(projectOptional.isPresent){
            project = projectOptional.get()
            project.url = repository.homepage
        }else{
            project = Project(
                source = source,
                url = repository.homepage,
                externalIdentifier = repository.id.toString()
            )
        }
        project.setMetadata("description", repository.description)
        project.setMetadata("forks_count", repository.forksCount)
        project.setMetadata("full_name", repository.fullName)
        project.setMetadata("git_transport_url", repository.gitTransportUrl)
        project.setMetadata("http_transport_url", repository.httpTransportUrl)
        project.setMetadata("allow_merge_commit", repository.isAllowMergeCommit)
        project.setMetadata("allow_rebase_merge", repository.isAllowRebaseMerge)
        project.setMetadata("allow_squash_merge", repository.isAllowSquashMerge)
        project.setMetadata("fork", repository.isFork)
        project.setMetadata("archived", repository.isArchived)
        project.setMetadata("delete_branch_on_merge", repository.isDeleteBranchOnMerge)
        project.setMetadata("private", repository.isPrivate)
        project.setMetadata("template", repository.isTemplate)
        project.setMetadata("language", repository.language)
        project.setMetadata("mirror_url", repository.mirrorUrl)
        project.setMetadata("url", repository.url)
        project.setMetadata("homepage", repository.homepage)
        project.setMetadata("owner_name", repository.ownerName)
        project.setMetadata("has_wiki", repository.hasWiki())
        project.setMetadata("has_projects", repository.hasProjects())
        project.setMetadata("has_pages", repository.hasPages())
        project.setMetadata("has_issues", repository.hasIssues())
        project.setMetadata("has_downloads", repository.hasDownloads())
        project.setMetadata("has_admin_access", repository.hasAdminAccess())
        project.setMetadata("has_pull_access", repository.hasPullAccess())
        project.setMetadata("has_push_access", repository.hasPushAccess())
        project.setMetadata("size", repository.getSize())
        project.setMetadata("ssh_url", repository.sshUrl)
        project.setMetadata("star_count", repository.stargazersCount)
        project.setMetadata("visibility_name", repository.visibility.name)
        project.setMetadata("open_issue_count", repository.openIssueCount)
        project.setMetadata("pushed_at", repository.pushedAt)
        project.setMetadata("watchers_count", repository.watchersCount)
        projectRepository.save(project)
        return project
    }
}

private fun Version.setMetadata(key: String, value: Any?) {
    for(obj in metadata){
        if(obj.key==key){
            if(value==null){
                obj.value=""
                obj.type=""
                return
            }
            obj.value = value.toString()
            obj.type = value::class.java.typeName
            return
        }
    }
    val versionMetadata: VersionMetadata = if(value == null){
        VersionMetadata(key = key, value = "", type = "", version = this)
    }else{
        VersionMetadata(key = key, value = value.toString(), type = value::class.java.typeName, version = this)
    }
    metadata.add(versionMetadata)
}

private fun Project.setMetadata(key: String, value: Any?) {
    for(obj in metadata){
        if(obj.key==key){
            if(value==null){
                obj.value=""
                obj.type=""
                return
            }
            obj.value = value.toString()
            obj.type = value::class.java.typeName
            return
        }
    }
    val projectMetadata: ProjectMetadata = if(value == null){
        ProjectMetadata(key = key, value = "", type = "", project = this)
    }else{
        ProjectMetadata(key = key, value = value.toString(), type = value::class.java.typeName, project = this)
    }
    metadata.add(projectMetadata)
}
