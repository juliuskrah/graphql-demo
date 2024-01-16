package com.example.graph.repository

import com.mongodb.reactivestreams.client.MongoClient
import io.mongock.driver.mongodb.reactive.driver.MongoReactiveDriver
import io.mongock.runner.springboot.EnableMongock
import org.assertj.core.api.Assertions.assertThat
import org.bson.Document
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.boot.testcontainers.service.connection.ServiceConnection
import org.springframework.context.annotation.Bean
import org.springframework.data.domain.ReactiveAuditorAware
import org.springframework.data.mongodb.config.EnableReactiveMongoAuditing
import org.springframework.data.mongodb.core.ReactiveMongoTemplate
import org.testcontainers.containers.MongoDBContainer
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers
import reactor.core.publisher.Mono
import reactor.test.StepVerifier
import reactor.test.StepVerifier.FirstStep
import java.net.URI
import java.time.Duration
import java.time.Instant

@Testcontainers
@DataMongoTest(properties = ["spring.data.mongodb.database=testDatabase"])
class ProductTest {
    @Autowired
    lateinit var productRepository: Products
    @Autowired
    lateinit var mongoDb: ReactiveMongoTemplate

    companion object {
        @JvmStatic
        @Container
        @ServiceConnection
        val mongo = MongoDBContainer("mongo:7.0.5-jammy")
    }

    @Test
    fun `should iterate window results`() {
        // TODO test for ScrollPosition
        this.productRepository.findAll().`as`(StepVerifier::create)
            .assertNext {
                assertThat(it.title).isEqualTo("Apple Juice")
            }
            .assertNext {
                assertThat(it.mediaUrls).hasSize(2)
            }
            .expectNextCount(1)
            .verifyComplete()
    }

    @Test
    fun `should validate title`() {
        val product = ProductEntity(description = "This will fail validation", mediaUrls = arrayListOf(
            URI("http://localhost:8080/failed.jpg")
        ))
        val step = this.mongoDb.save(product).`as`(StepVerifier::create)
        failedVerification(step,
            "Document failed validation",
            "Product Object validation",
            "\"missingProperties\": [\"title\"]")
    }

    @Test
    fun `should validate createdAt`() {
        val product = Document("description", "This will fail validation")
            .append("createdBy", "no one")
            .append("updatedBy", "no one")
            .append("title", "validation-error")
            .append("updatedAt", Instant.now())
        val step = this.mongoDb.getCollection("products").flatMap {
            Mono.from(it.insertOne(product))
        }.`as`(StepVerifier::create)
        failedVerification(step, "Document failed validation",
            "Product Object validation", "\"missingProperties\": [\"createdAt\"]")
    }

    @Test
    fun `should validate updatedAt`() {
        val product = Document("description", "This will fail validation")
            .append("createdBy", "no one")
            .append("updatedBy", "no one")
            .append("title", "validation-error")
            .append("createdAt", Instant.now())
        val step = this.mongoDb.getCollection("products").flatMap {
            Mono.from(it.insertOne(product))
        }.`as`(StepVerifier::create)
        failedVerification(step, "Document failed validation",
            "Product Object validation", "\"missingProperties\": [\"updatedAt\"]")
    }

    @Test
    fun `should validate createdBy`() {
        val now = Instant.now()
        val product = Document("description", "This will fail validation")
            .append("updatedBy", "no one")
            .append("title", "validation-error")
            .append("updatedAt", now)
            .append("createdAt", now)
        val step = this.mongoDb.getCollection("products").flatMap {
            Mono.from(it.insertOne(product))
        }.`as`(StepVerifier::create)
        failedVerification(step, "Document failed validation",
            "Product Object validation", "\"missingProperties\": [\"createdBy\"]")
    }

    @Test
    fun `should validate updatedBy`() {
        val now = Instant.now()
        val product = Document("description", "This will fail validation")
            .append("createdBy", "no one")
            .append("title", "validation-error")
            .append("updatedAt", now)
            .append("createdAt", now)
        val step = this.mongoDb.getCollection("products").flatMap {
            Mono.from(it.insertOne(product))
        }.`as`(StepVerifier::create)
        failedVerification(step, "Document failed validation",
            "Product Object validation", "\"missingProperties\": [\"updatedBy\"]")
    }

    private fun <T> failedVerification(step: FirstStep<T>, vararg messages: CharSequence) {
        step.expectErrorSatisfies { throwable ->
            assertThat(throwable).hasMessageContainingAll(*messages)
        }.verify(Duration.ofSeconds(1))
    }

    @EnableMongock
    @EnableReactiveMongoAuditing(auditorAwareRef = "testAuditor")
    @TestConfiguration(proxyBeanMethods = false)
    internal class TestMongockConfiguration {
        @Bean
        fun testDriver(client: MongoClient, @Value("\${spring.data.mongodb.database}") database: String): MongoReactiveDriver? {
            return MongoReactiveDriver.withDefaultLock(client, database)
        }

        @Bean
        fun testAuditor(): ReactiveAuditorAware<String> = ReactiveAuditorAware {
            Mono.just("test user")
        }
    }
}