package net.kikkirej.alexandria.initiator.github

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class GithubInitiatorApplication

fun main(args: Array<String>) {
	runApplication<GithubInitiatorApplication>(*args)
}
