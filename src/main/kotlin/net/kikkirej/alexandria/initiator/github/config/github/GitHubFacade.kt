package net.kikkirej.alexandria.initiator.github.config.github

import net.kikkirej.alexandria.initiator.github.config.GitHubInitConfig
import net.kikkirej.alexandria.initiator.github.config.GitHubSourceConfig
import org.kohsuke.github.GHProject
import org.kohsuke.github.GitHub
import org.kohsuke.github.GitHubBuilder
import org.kohsuke.github.PagedIterable
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class GitHubFacade(@Autowired val gitHubInitConfig: GitHubInitConfig)  {

    private fun githubConnection(source: GitHubSourceConfig): GitHub {
        val gitHubBuilder = GitHubBuilder()
        if(source.organization == null){
            gitHubBuilder.withOAuthToken(source.accessToken)
        }else{
            gitHubBuilder.withOAuthToken(source.accessToken, source.organization)
        }
        return gitHubBuilder.build()
    }

    fun gitHubProjectsIn(source: GitHubSourceConfig): List<GHProject> {
        val github = githubConnection(source)
        val projects: PagedIterable<GHProject>?
        if(source.organization != null) {
            val organization = github.getOrganization(source.organization)
            projects = organization.listProjects()
        }else{
            projects = github.myself.listProjects()
        }
        if(source.namePattern == null){
            return projects.toList()
        }
        val resultlist = mutableListOf<GHProject>()
        for (project in projects){
            val projectFitsCriteria: Boolean = checkIfProjectFitsCriteria(project, source)
            if (projectFitsCriteria) {
                resultlist.add(project)
            }
        }
        return resultlist.toList(); // no more mutable
    }

    private fun checkIfProjectFitsCriteria(project: GHProject?, source: GitHubSourceConfig): Boolean {
        if(source.namePattern == null){
            return true
        }
        val namePattern: String = source.namePattern!!
        if(project!!.name.matches(namePattern)){

        }
    }
}