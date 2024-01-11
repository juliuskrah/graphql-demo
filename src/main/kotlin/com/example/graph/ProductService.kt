package com.example.graph

import com.example.graph.generated.types.Product
import com.example.graph.generated.types.ProductInput
import org.springframework.data.domain.ScrollPosition
import org.springframework.data.domain.Window
import reactor.core.publisher.Mono

/**
 * @author Julius Krah
 */
interface ProductService: NodeService {
    fun product(id: String): Mono<Product>

    fun product(product: ProductInput): Mono<Product>

    fun products(count: Int, scroll: ScrollPosition): Mono<Window<Product>>
}
