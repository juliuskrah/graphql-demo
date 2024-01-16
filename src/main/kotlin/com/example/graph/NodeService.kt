package com.example.graph

import com.example.graph.generated.types.Node
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

/**
 * @author Julius Krah
 */
interface NodeService {

    fun node(id: String): Mono<out Node>

    fun nodes(ids: List<String>): Flux<out Node>
}
