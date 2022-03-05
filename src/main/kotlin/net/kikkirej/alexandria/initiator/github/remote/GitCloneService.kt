package net.kikkirej.alexandria.initiator.github.remote

import net.kikkirej.alexandria.initiator.github.config.GitHubSourceConfig
import org.eclipse.jgit.api.Git
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider
import org.springframework.stereotype.Service
import java.io.File

@Service
class GitCloneService {
    fun clone(destination: String, cloneUrl: String, branch: String, source: GitHubSourceConfig){
        val cloneCommand = Git.cloneRepository()
        cloneCommand.setBranch(branch)
        cloneCommand.setDirectory(File(destination))
        cloneCommand.setURI(cloneUrl)
        cloneCommand.setCredentialsProvider(UsernamePasswordCredentialsProvider(source.accessUsername,source.accessToken))
        cloneCommand.call()
    }
}