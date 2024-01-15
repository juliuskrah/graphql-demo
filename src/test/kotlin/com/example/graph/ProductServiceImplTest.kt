package com.example.graph

import com.example.graph.repository.Products
import com.example.graph.support.ProductNotFoundException
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.mockito.kotlin.mock
import org.springframework.context.support.ResourceBundleMessageSource
import reactor.test.StepVerifier
import reactor.util.context.Context
import java.util.Locale

/**
 * @author Julius Krah
 */
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ProductServiceImplTest {
    private lateinit var productService: ProductServiceImpl

    @BeforeAll
    fun init() {
        val productRepository = mock<Products>()
        productService = ProductServiceImpl(productRepository)
        val messageSource = ResourceBundleMessageSource()
        messageSource.setBasenames("messages/messages")
        productService.setMessageSource(messageSource)
    }

    @Test
    fun `should fetch product`() {
        val id = "Z2lkOi8vZGVtby9Qcm9kdWN0LzcxZTg5Nzc3LTI0ZmItNDA5MC04YTI3LTE0NzU2ZGQ2OWI3MQ"
        val product = productService.product(id).contextWrite(Context.of(Locale::class, Locale.ENGLISH))

        StepVerifier.create(product)
            .consumeNextWith {
                assertThat(it).isNotNull()
                    .hasFieldOrPropertyWithValue("id", "Z2lkOi8vZGVtby9Qcm9kdWN0LzcxZTg5Nzc3LTI0ZmItNDA5MC04YTI3LTE0NzU2ZGQ2OWI3MQ==")
                    .hasFieldOrPropertyWithValue("title", "Phone Case")
            }.expectComplete().verify()
    }

    @Test
    fun `should fetch node`() {
        val product = productService.node("Z2lkOi8vZGVtby9Qcm9kdWN0LzcxMDA2YWZlLTFkMDctNDYwYi1hN2M3LWRhNGNkNzkwNWZlMA")
            .contextWrite(Context.of(Locale::class, Locale.GERMAN))

        StepVerifier.create(product)
            .consumeNextWith {
                assertThat(it).isNotNull()
                    .hasFieldOrPropertyWithValue("id", "Z2lkOi8vZGVtby9Qcm9kdWN0LzcxMDA2YWZlLTFkMDctNDYwYi1hN2M3LWRhNGNkNzkwNWZlMA==")
                    .hasFieldOrPropertyWithValue("title", "Taschenrechner")
            }.expectComplete().verify()
    }

    @Test
    fun `should fail product`() {
        val product = productService.product("Z2lkOi8vZGVtby9Qcm9kdWN0LzU3Nzc0MzU4LTM2YzQtNDFmMC04YzBlLTQyNjBlZDgwYjE5ZA==")

        StepVerifier.create(product)
            .consumeErrorWith {
                assertThat(it).isInstanceOf(ProductNotFoundException::class.java)
                    .hasMessage("exception.product.service.not-found")
            }.verify()
    }
}