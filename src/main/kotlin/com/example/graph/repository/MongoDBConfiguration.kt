package com.example.graph.repository

import com.example.graph.migration.`202401151530CreateProductCollectionChangeUnit`
import com.mongodb.reactivestreams.client.MongoClient
import io.mongock.api.config.MongockConfiguration
import io.mongock.driver.api.driver.ConnectionDriver
import io.mongock.driver.mongodb.reactive.driver.MongoReactiveDriver
import io.mongock.runner.core.executor.system.changes.SystemChangeUnit00001
import io.mongock.runner.springboot.MongockSpringboot
import io.mongock.runner.springboot.RunnerSpringbootBuilder
import io.mongock.runner.springboot.base.config.MongockContextBase
import io.mongock.runner.springboot.base.config.MongockSpringConfiguration
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.ApplicationContext
import org.springframework.context.ApplicationEventPublisher
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.domain.ReactiveAuditorAware
import org.springframework.data.mongodb.config.EnableReactiveMongoAuditing
import reactor.core.publisher.Mono
import io.mongock.api.config.MongockConfiguration as MongockConfig

/**
 * @author Julius Krah
 */
@Configuration(proxyBeanMethods = false)
@ConditionalOnProperty(value = ["\${mongock.enabled}"], havingValue = "true", matchIfMissing = true)
@EnableConfigurationProperties(value = [MongockSpringConfiguration::class])
@EnableReactiveMongoAuditing(auditorAwareRef = "auditor")
class MongoDBConfiguration: MongockContextBase<MongockConfiguration>() {

    @Bean
    fun connectionDriver(
        @Value("\${spring.data.mongodb.database}") database: String,
        config: MongockConfig, client: MongoClient,
    ): MongoReactiveDriver {
        val driver = MongoReactiveDriver.withLockStrategy(client, database,
            config.lockAcquiredForMillis,
            config.lockQuitTryingAfterMillis,
            config.lockTryFrequencyMillis
        )
        return driver
    }

    @Bean
    fun auditor(): ReactiveAuditorAware<String> {
        return ReactiveAuditorAware {
            Mono.just("system-job")
        }
    }

    override fun getBuilder(
        connectionDriver: ConnectionDriver,
        springConfiguration: MongockConfiguration,
        springContext: ApplicationContext,
        applicationEventPublisher: ApplicationEventPublisher,
    ): RunnerSpringbootBuilder {
        return MongockSpringboot.builder()
            .setDriver(connectionDriver)
            .setConfig(springConfiguration)
            .setSpringContext(springContext)
            .setEventPublisher(applicationEventPublisher)
            .addMigrationClass(SystemChangeUnit00001::class.java)
            .addMigrationClass(`202401151530CreateProductCollectionChangeUnit`::class.java)
    }
}
