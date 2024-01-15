package com.example.graph.repository

import com.mongodb.reactivestreams.client.MongoClient
import io.mongock.driver.mongodb.reactive.driver.MongoReactiveDriver
import io.mongock.runner.springboot.EnableMongock
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.boot.testcontainers.service.connection.ServiceConnection
import org.springframework.context.annotation.Bean
import org.springframework.data.domain.Example
import org.springframework.data.domain.ScrollPosition
import org.testcontainers.containers.MongoDBContainer
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers
import reactor.test.StepVerifier

@Testcontainers
@DataMongoTest(properties = ["spring.data.mongodb.database=testDatabase"])
class ProductTest {
    @Autowired
    lateinit var productRepository: Products

    companion object {
        @JvmStatic
        @Container
        @ServiceConnection
        val mongo = MongoDBContainer("mongo:7.0.5-jammy")
    }

    @Test
    fun `should iterate window results`() {
//        val count = 5
//        val scroll = ScrollPosition.keyset()
//        val window = this.productRepository.findBy(Example.of(ProductEntity())) { query ->
//            query.limit(count).scroll(scroll)
//        }
//        StepVerifier.create(window).consumeNextWith {
//            assertThat(it.size()).isEqualTo(3)
//        }.verifyComplete()
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

    @EnableMongock
    @TestConfiguration(proxyBeanMethods = false)
    internal class TestMongockConfiguration {
        @Bean
        fun testDriver(client: MongoClient, @Value("\${spring.data.mongodb.database}") database: String): MongoReactiveDriver? {
            return MongoReactiveDriver.withDefaultLock(client, database)
        }
    }
}