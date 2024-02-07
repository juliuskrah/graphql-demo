package com.example.graph.repository

import com.example.graph.spring.EnableMongockChangeUnit
import com.mongodb.reactivestreams.client.MongoClient
import io.mongock.api.annotations.ChangeUnit
import io.mongock.driver.mongodb.reactive.driver.MongoReactiveDriver
import io.mongock.runner.springboot.RunnerSpringbootBuilder
import io.mongock.runner.springboot.base.MongockApplicationRunner
import io.mongock.runner.springboot.base.MongockInitializingBeanRunner
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression
import org.springframework.context.ApplicationContext
import org.springframework.context.ApplicationEventPublisher
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.ComponentScan.Filter
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.FilterType
import org.springframework.data.domain.ReactiveAuditorAware
import org.springframework.data.mongodb.config.EnableReactiveMongoAuditing
import org.springframework.data.mongodb.core.mapping.Document
import reactor.core.publisher.Mono
import io.mongock.api.config.MongockConfiguration as MongockConfig

/**
 * @author Julius Krah
 */
@Configuration(proxyBeanMethods = false)
@EnableMongockChangeUnit(basePackages = ["com.example.graph"],
    includeFilters = [Filter(type = FilterType.ANNOTATION, classes = [ChangeUnit::class])],
    excludeFilters = [Filter(Document::class)]
)
@EnableReactiveMongoAuditing(auditorAwareRef = "auditor")
class MongoDBConfiguration {
    @Autowired
    lateinit var applicationContext: ApplicationContext
    @Autowired
    lateinit var applicationEventPublisher: ApplicationEventPublisher

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
    @ConditionalOnExpression("'\${mongock.runner-type:ApplicationRunner}'.toLowerCase().equals('applicationrunner')")
    fun applicationRunner(mongockRunner: RunnerSpringbootBuilder): MongockApplicationRunner {
        mongockRunner.setSpringContext(applicationContext).setEventPublisher(applicationEventPublisher)
        return mongockRunner.buildApplicationRunner()
    }

    @Bean
    @ConditionalOnExpression("'\${mongock.runner-type:null}'.toLowerCase().equals('initializingbean')")
    fun initializingBeanRunner(mongockRunner: RunnerSpringbootBuilder): MongockInitializingBeanRunner {
        mongockRunner.setSpringContext(applicationContext).setEventPublisher(applicationEventPublisher)
        return mongockRunner.buildInitializingBeanRunner()
    }

    @Bean
    fun auditor(): ReactiveAuditorAware<String> {
        return ReactiveAuditorAware {
            Mono.just("system-job")
        }
    }

}
