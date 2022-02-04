package net.kikkirej.alexandria.initiator.github

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class GitHubInitiatorApplication

fun main(args: Array<String>) {
	runApplication<GitHubInitiatorApplication>(*args)
}
