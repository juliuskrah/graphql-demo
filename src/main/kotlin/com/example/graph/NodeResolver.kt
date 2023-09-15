package com.example.graph

import com.example.graph.generated.types.Node
import org.springframework.stereotype.Component
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

/**
 * @author Julius Krah
 */
@Component
class NodeResolver(val nodeServices: Map<String, NodeService>) {
    fun nodeById(id: String): Mono<out Node> {
        val (node, _) = GlobalId.from(id)
        val nodeService = nodeServices[node] ?: throw IllegalArgumentException("exception.node.resolver.missing-implementation")
        return nodeService.node(id)
    }

    fun nodeByIds(ids: List<String>): Flux<out Node> {
        TODO("Not implemented yet")
    }
}