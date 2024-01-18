package com.example.graph

import com.example.graph.repository.ProductEntity
import com.example.graph.repository.Products
import com.example.graph.support.ProductNotFoundException
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.mockito.ArgumentMatchers.anyString
import org.mockito.kotlin.doAnswer
import org.mockito.kotlin.mock
import reactor.core.publisher.Mono
import reactor.test.StepVerifier
import java.time.Instant.now

/**
 * @author Julius Krah
 */
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ProductServiceTest {
    private lateinit var productService: ProductServiceImpl

    @BeforeAll
    fun init() {
        val productRepository = mock<Products> {
            on { findById(anyString()) } doAnswer {
                when(it.getArgument(0, String::class.java)) {
                    "57774358-36c4-41f0-8c0e-4260ed80b19" -> Mono.empty()
                    else -> Mono.just(ProductEntity(
                        id = "71e89777-24fb-4090-8a27-14756dd69b7",
                        title = "Phone Case",
                        createdAt = now(),
                        updatedAt = now()
                    ))
                }
            }
        }
        productService = ProductServiceImpl(productRepository)
    }

    @Test
    fun `should fetch product`() {
        val id = "71e89777-24fb-4090-8a27-14756dd69b7"
        val product = productService.product(id)

        StepVerifier.create(product)
            .consumeNextWith {
                assertThat(it).isNotNull()
                    .hasFieldOrPropertyWithValue("id", "71e89777-24fb-4090-8a27-14756dd69b7")
                    .hasFieldOrPropertyWithValue("title", "Phone Case")
            }.expectComplete().verify()
    }

    @Test
    fun `should fetch node`() {
        val product = productService.node("71e89777-24fb-4090-8a27-14756dd69b7")

        StepVerifier.create(product)
            .consumeNextWith {
                assertThat(it).isNotNull()
                    .hasFieldOrPropertyWithValue("id", "71e89777-24fb-4090-8a27-14756dd69b7")
                    .hasFieldOrPropertyWithValue("title", "Phone Case")
            }.expectComplete().verify()
    }

    @Test
    fun `should fail product`() {
        val product = productService.product("57774358-36c4-41f0-8c0e-4260ed80b19")

        StepVerifier.create(product)
            .consumeErrorWith {
                assertThat(it).isInstanceOf(ProductNotFoundException::class.java)
                    .hasMessage("exception.product.service.not-found")
            }.verify()
    }
}