package com.example.graph

import com.example.graph.generated.types.Product
import com.example.graph.generated.types.ProductInput
import reactor.core.publisher.Mono

/**
 * @author Julius Krah
 */
interface ProductService: NodeService {
    fun product(id: String): Mono<Product>

    fun product(product: ProductInput): Mono<Product>
}