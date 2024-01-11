package com.example.graph

import com.example.graph.generated.types.Node
import com.example.graph.support.GlobalId
import org.springframework.stereotype.Component
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

/**
 * @author Julius Krah
 */
@Component
class NodeResolver(val nodeServices: Map<String, NodeService>) {
    fun nodeById(id: String): Mono<out Node> {
        val (node, actualId) = GlobalId.from(id)
        val nodeService = nodeServices[node] ?: throw IllegalArgumentException("exception.node.resolver.missing-implementation")
        return nodeService.node(actualId)
    }

    fun nodeByIds(ids: List<String>): Flux<out Node> {
        TODO("Not implemented yet")
    }
}
