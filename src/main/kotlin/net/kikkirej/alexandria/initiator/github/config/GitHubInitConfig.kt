package net.kikkirej.alexandria.initiator.github.config

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Configuration

@Configuration
@ConfigurationProperties("alexandria.initiator.github")
class GitHubInitConfig {
    var sources: List<GitHubSourceConfig> = listOf()
    var cron: String = "5/* * * * * *"
}

class GitHubSourceConfig(
    var id: Long,
    var name: String,
    var organization: String?,
    var accessUsername: String,
    var accessToken: String,
    var repositoryNamePattern: String?,
    val branchNamePattern: String?,
) {

}
