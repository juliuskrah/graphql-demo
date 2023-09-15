package com.example.graph

import com.example.graph.generated.types.Product
import reactor.core.publisher.Mono

/**
 * @author Julius Krah
 */
interface ProductService: NodeService {
    fun product(id: String): Mono<Product>
}