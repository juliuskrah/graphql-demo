package com.example.graph

import com.example.graph.generated.types.Node
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

/**
 * @author Julius Krah
 */
@Service
class NodeServiceImpl: NodeService {

    override fun node(id: String): Mono<Node> {
        TODO("Not yet implemented, bruh!")
    }

    override fun nodes(ids: List<String>): Flux<Node> {
        TODO("Not yet implemented, bruh!")
    }
}