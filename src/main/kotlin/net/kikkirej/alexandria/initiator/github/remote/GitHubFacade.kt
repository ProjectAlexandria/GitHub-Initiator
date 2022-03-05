package net.kikkirej.alexandria.initiator.github.remote

import net.kikkirej.alexandria.initiator.github.config.GitHubSourceConfig
import org.kohsuke.github.*
import org.springframework.stereotype.Service

@Service
class GitHubFacade  {

    private fun githubConnection(source: GitHubSourceConfig): GitHub {
        val gitHubBuilder = GitHubBuilder()
        if(source.organization == null){
            gitHubBuilder.withOAuthToken(source.accessToken)
        }else{
            gitHubBuilder.withOAuthToken(source.accessToken, source.organization)
        }
        return gitHubBuilder.build()
    }

    fun getAnalyzingObjectsForSource(source: GitHubSourceConfig): Map<GHRepository, List<GHBranch>>{
        val resultMap = mutableMapOf<GHRepository, List<GHBranch>>()
        val github = githubConnection(source)
        val repositories = gitHubRepositories(github, source)
        for (repository in repositories){
            val branches = filterBranches(source, repository.branches, repository.defaultBranch)
            resultMap[repository] = branches
        }
        return resultMap
    }

    private fun filterBranches(source: GitHubSourceConfig, branches: Map<String, GHBranch>, defaultBranch: String): List<GHBranch> {
        val resultList = mutableListOf<GHBranch>()
        for(branchName in branches.keys){
            if(source.branchNamePattern == null || branchName == defaultBranch || branchName.matches(Regex(source.branchNamePattern.toString()))){
                val ghBranch = branches[branchName]
                if (ghBranch != null) {
                    resultList.add(ghBranch)
                }
            }
        }
        return resultList.toList()
    }

    private fun gitHubRepositories(github: GitHub, source: GitHubSourceConfig): List<GHRepository> {
        val repositories: PagedIterable<GHRepository>?
        if(source.organization != null) {
            val organization = github.getOrganization(source.organization)
            repositories = organization.listRepositories()
        }else{
            repositories = github.myself.listRepositories()
        }
        if(source.repositoryNamePattern == null){
            return repositories.toList()
        }
        val resultList = mutableListOf<GHRepository>()
        for (repository in repositories){
            val projectFitsCriteria: Boolean = checkIfProjectFitsCriteria(repository, source)
            if (projectFitsCriteria) {
                resultList.add(repository)
            }
        }
        return resultList.toList() // no more mutable
    }

    private fun checkIfProjectFitsCriteria(repository: GHRepository?, source: GitHubSourceConfig): Boolean {
        if(source.repositoryNamePattern == null){
            return true
        }
        if(repository?.name?.matches(Regex(source.repositoryNamePattern.toString())) == true){
            return true
        }
        return false
    }
}