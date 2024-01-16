package com.example.graph

import com.example.graph.generated.DgsConstants
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.condition.DisabledInNativeImage
import org.mockito.Mockito.anyString
import org.mockito.kotlin.check
import org.mockito.kotlin.doAnswer
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import reactor.core.publisher.Mono
import reactor.test.StepVerifier
import java.time.Duration

/**
 * @author Julius Krah
 */
@DisabledInNativeImage
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class NodeResolverTest {

    private lateinit var nodeServices: Map<String, NodeService>
    private lateinit var productService: ProductService
    private lateinit var nodeResolver: NodeResolver

    @BeforeAll
    fun setUp() {
        productService = mock<ProductService>()
        nodeServices = mock<Map<String, NodeService>> {
            on { get(anyString()) } doAnswer {
                if (it.arguments[0] == DgsConstants.PRODUCT.TYPE_NAME)
                    productService
                else
                    null
            }
        }
        whenever(productService.node(anyString())).thenReturn(Mono.empty())
        nodeResolver = NodeResolver(nodeServices)
    }

    @Test
    fun `should fetch node by id`() {
        // gid://demo/Product/f5c1287c-0bc5-4b05-8af4-5fe7d3cbc99e
        val id = "Z2lkOi8vZGVtby9Qcm9kdWN0L2Y1YzEyODdjLTBiYzUtNGIwNS04YWY0LTVmZTdkM2NiYzk5ZQ"
        val decodedId = "f5c1287c-0bc5-4b05-8af4-5fe7d3cbc99e"
        val node = nodeResolver.nodeById(id)

        StepVerifier.create(node).verifyComplete()
        verify(productService).node(check {
            assertThat(it).isEqualTo(decodedId)
        })
    }

    @Test
    fun `should fail with exception`() {
        // gid://demo/Order/f5c1287c-0bc5-4b05-8af4-5fe7d3cbc99e
        val id = "Z2lkOi8vZGVtby9PcmRlci9mNWMxMjg3Yy0wYmM1LTRiMDUtOGFmNC01ZmU3ZDNjYmM5OWU"

        nodeResolver.nodeById(id).`as`(StepVerifier::create)
            .expectErrorSatisfies { throwable ->
                assertThat(throwable).isInstanceOf(IllegalArgumentException::class.java)
                    .hasMessage("exception.node.resolver.missing-implementation")
            }.verify(Duration.ofSeconds(1))
    }
}