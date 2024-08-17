package com.release_manager

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.cache.annotation.EnableCaching

@SpringBootApplication
@EnableCaching
class ReleaseManagerApplication

fun main(args: Array<String>) {
	runApplication<ReleaseManagerApplication>(*args)
}
