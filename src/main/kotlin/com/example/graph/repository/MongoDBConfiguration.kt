package com.example.graph.repository

import com.mongodb.reactivestreams.client.MongoClient
import io.mongock.driver.mongodb.reactive.driver.MongoReactiveDriver
import io.mongock.runner.springboot.EnableMongock
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.domain.AuditorAware
import org.springframework.data.domain.ReactiveAuditorAware
import org.springframework.data.mongodb.config.EnableReactiveMongoAuditing
import reactor.core.publisher.Mono
import io.mongock.api.config.MongockConfiguration as MongockConfig

/**
 * @author Julius Krah
 */
@EnableMongock
@Configuration(proxyBeanMethods = false)
@EnableReactiveMongoAuditing(auditorAwareRef = "auditor")
class MongoDBConfiguration {

    @Bean
    fun connectionDriver(@Value("\${spring.data.mongodb.database}") database: String,
        config: MongockConfig, client: MongoClient): MongoReactiveDriver {
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
            Mono.just("system")
        }
    }
}