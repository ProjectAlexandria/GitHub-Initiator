package net.kikkirej.alexandria.initiator.github

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.scheduling.annotation.EnableScheduling

@SpringBootApplication
@EnableScheduling
class GitHubInitiatorApplication

fun main(args: Array<String>) {
	runApplication<GitHubInitiatorApplication>(*args)
}
