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
    var id: Long =-1,
    var name: String="dummy",
    var organization: String?="dummy",
    var accessUsername: String="dummy",
    var accessToken: String="dummy",
    var repositoryNamePattern: String? = null,
    val branchNamePattern: String? = null,
)
