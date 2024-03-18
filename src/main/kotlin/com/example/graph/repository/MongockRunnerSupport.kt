package com.example.graph.repository

import io.mongock.api.config.MongockConfiguration
import io.mongock.driver.api.driver.ConnectionDriver
import io.mongock.runner.core.executor.MongockRunner
import io.mongock.runner.springboot.MongockSpringboot
import io.mongock.runner.springboot.RunnerSpringbootBuilder
import org.springframework.context.ApplicationContext
import org.springframework.context.ApplicationContextAware
import org.springframework.context.ApplicationEventPublisher
import org.springframework.context.ApplicationEventPublisherAware

/**
 * It is difficult using java bean properties when creating a BeanDefintion using MongockRunnerBuilder.
 * This support class enables creating a [org.springframework.beans.factory.config.BeanDefinition] programmatically for the MongockRunnerBuilder.
 * @author Julius Krah
 * @see MongockRunner
 * @see RunnerSpringbootBuilder
 */
class MongockRunnerSupport: ApplicationContextAware, ApplicationEventPublisherAware {
    var driver: ConnectionDriver? = null
    var config: MongockConfiguration? = null
    var migrationClasses: List<Class<*>>? = emptyList()
    private lateinit var applicationContext: ApplicationContext
    private lateinit var eventPublisher: ApplicationEventPublisher

    fun create(): MongockRunner {
        val builder: RunnerSpringbootBuilder = MongockSpringboot.builder()
        if (this.driver != null) builder.setDriver(driver)
        if (this.config != null) builder.setConfig(config)
        builder.setSpringContext(applicationContext)
        builder.setEventPublisher(eventPublisher)
        migrationClasses?.forEach(builder::addMigrationClass)
        return builder.buildRunner()
    }

    override fun setApplicationContext(applicationContext: ApplicationContext) {
        this.applicationContext = applicationContext
    }

    override fun setApplicationEventPublisher(applicationEventPublisher: ApplicationEventPublisher) {
        this.eventPublisher = applicationEventPublisher
    }
}