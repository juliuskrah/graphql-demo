package com.example.graph

import com.example.graph.generated.types.Node
import com.example.graph.generated.types.Product
import org.assertj.core.api.Assertions.`as`
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.InstanceOfAssertFactories
import org.assertj.core.api.InstanceOfAssertFactories.type
import org.junit.jupiter.api.Test
import org.mockito.ArgumentMatchers.anyString
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.graphql.GraphQlProperties
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.test.autoconfigure.graphql.tester.AutoConfigureHttpGraphQlTester
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.graphql.ResponseError
import org.springframework.graphql.execution.ErrorType
import org.springframework.graphql.test.tester.HttpGraphQlTester
import org.springframework.security.test.context.support.WithMockUser
import org.springframework.test.web.reactive.server.WebTestClient
import reactor.core.publisher.Mono
import java.time.OffsetDateTime
import java.util.Locale

/**
 * @author Julius Krah
 */
@AutoConfigureHttpGraphQlTester
@EnableConfigurationProperties(GraphQlProperties::class)
@SpringBootTest(
    webEnvironment = SpringBootTest.WebEnvironment.MOCK,
    properties = [
        "spring.autoconfigure.exclude=org.springframework.boot.autoconfigure.mongo.MongoAutoConfiguration",
        "mongock.enabled=false",
        "spring.main.allow-bean-definition-overriding=true"
    ]
)
@WithMockUser
class NodeControllerTest {

    @Test
    fun `should fetch node by ID`(@Autowired graphQlTester: HttpGraphQlTester) {
        val nodeId = "Z2lkOi8vZGVtby9Qcm9kdWN0LzU3MWEyMzc2LTI0YjYtNGIwNS04YzkwLTdkMzhjZDA0MWJmZA"
        val localeAwareTester = graphQlTester.mutate().webTestClient { client: WebTestClient.Builder ->
            client.defaultHeaders { headers ->
                headers.acceptLanguage = listOf(Locale.LanguageRange("de-DE", 1.0))
            }
        }.build()
        localeAwareTester.documentName("nodeDetails")
            .variable("id", nodeId)
            .execute()
            .path("node").entity(Node::class.java)
            .satisfies { node ->
                assertThat(node).isNotNull()
                    .extracting(Node::id, `as`(InstanceOfAssertFactories.STRING)).isBase64()
                    .isEqualTo("Z2lkOi8vZGVtby9Qcm9kdWN0LzU3MWEyMzc2LTI0YjYtNGIwNS04YzkwLTdkMzhjZDA0MWJmZA==")
                assertThat(node).hasFieldOrPropertyWithValue("title", "headphones")

            }
    }

    @Test
    fun `should fail node by ID`(@Autowired graphQlTester: HttpGraphQlTester) {
        val nodeId = "Z2lkOi8vZGVtby9PcmRlci83MWU4OTc3Ny0yNGZiLTQwOTAtOGEyNy0xNDc1NmRkNjliNzE"
        graphQlTester.documentName("nodeDetails")
            .variable("id", nodeId)
            .execute().errors()
            .expect { error ->
                error.errorType == ErrorType.BAD_REQUEST
            }.satisfy { errors ->
                assertThat(errors).isNotEmpty.hasSize(1).first(type(ResponseError::class.java))
                    .hasFieldOrPropertyWithValue("extensions", mapOf("classification" to "BAD_REQUEST"))
                    .hasFieldOrPropertyWithValue("message", "The Node with global id $nodeId is unknown in our system")
                    .hasFieldOrPropertyWithValue("path", "node")
            }.path("node").valueIsNull()
    }

    @Test
    fun `should fetch product by ID`(@Autowired graphQlTester: HttpGraphQlTester) {
        val nodeId = "Z2lkOi8vZGVtby9Qcm9kdWN0LzcxZTg5Nzc3LTI0ZmItNDA5MC04YTI3LTE0NzU2ZGQ2OWI3MQ"
        val localeAwareTester = graphQlTester.mutate().webTestClient { client: WebTestClient.Builder ->
            client.defaultHeaders { headers ->
                headers.acceptLanguage = listOf(Locale.LanguageRange("de-DE", 1.0))
            }
        }.build()
        localeAwareTester.documentName("productDetails")
            .variable("id", nodeId)
            .execute()
            .path("product").entity(Product::class.java)
            .satisfies { node ->
                assertThat(node).isNotNull()
                    .extracting(Product::id, `as`(InstanceOfAssertFactories.STRING)).isBase64()
                    .isEqualTo("Z2lkOi8vZGVtby9Qcm9kdWN0LzcxZTg5Nzc3LTI0ZmItNDA5MC04YTI3LTE0NzU2ZGQ2OWI3MQ==")
                assertThat(node).extracting(Product::title).isEqualTo("Smartphone case")
            }
    }

    @TestConfiguration(proxyBeanMethods = false)
    internal class NodeControllerConfiguration {

        @Bean(name = ["Product"])
        fun nodeService() = mock<ProductService> {
            val now = OffsetDateTime.now()
            on { product(anyString()) } doReturn  Mono.just(Product(
                id = "71e89777-24fb-4090-8a27-14756dd69b71",
                title = "Smartphone case",
                createdAt = now,
                updatedAt = now
            ))
            on { node(anyString()) } doReturn Mono.just(Product(
                id = "571a2376-24b6-4b05-8c90-7d38cd041bfd",
                title = "headphones",
                createdAt = now,
                updatedAt = now
            ))
        }
    }
}