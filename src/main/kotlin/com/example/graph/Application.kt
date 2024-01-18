package com.example.graph

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

/**
 * @author Julius Krah
 */
@SpringBootApplication(proxyBeanMethods = false)
class Application

fun main(args: Array<String>) {
    runApplication<Application>(*args)
}
