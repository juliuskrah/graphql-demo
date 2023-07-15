package com.example.graph

import com.example.graph.generated.types.Node
import graphql.GraphQLError
import graphql.schema.DataFetchingEnvironment
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.reactive.asFlow
import kotlinx.coroutines.reactor.awaitSingleOrNull
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.graphql.data.method.annotation.Argument
import org.springframework.graphql.data.method.annotation.GraphQlExceptionHandler
import org.springframework.graphql.data.method.annotation.QueryMapping
import org.springframework.graphql.execution.ErrorType
import org.springframework.stereotype.Controller
import reactor.core.publisher.Mono

/**
 * @author Julius Krah
 */
@Controller
class NodeController(
    private val nodeService: NodeService
) {

    val log: Logger = LoggerFactory.getLogger(NodeController::class.java)

    @QueryMapping
    suspend fun node(@Argument id: String): Node? {
        log.info("Using node id: {}", id)
        return nodeService.node(id).awaitSingleOrNull()
    }

    @QueryMapping
    fun nodes(@Argument ids: List<String>): Flow<Node> {
        log.info("Using node ids: {}", ids)
        return nodeService.nodes(ids).asFlow()
    }

    @GraphQlExceptionHandler
    suspend fun handle(ex: Throwable, env: DataFetchingEnvironment): GraphQLError = coroutineScope {
        log.info("Handling error coroutine")
        GraphQLError.newError()
            .errorType(ErrorType.BAD_REQUEST)
            .message(ex.message)
            .build()

    }

    // This works
    // @GraphQlExceptionHandler
    fun handleT(ex: Throwable, env: DataFetchingEnvironment): GraphQLError {
        log.info("Handling error imperative")
        return GraphQLError.newError()
            .errorType(ErrorType.INTERNAL_ERROR)
            .message(ex.message)
            .build()
    }

    // This works as well
    // @GraphQlExceptionHandler
    fun handleMono(ex: Throwable, env: DataFetchingEnvironment): Mono<GraphQLError> {
        log.info("Handling error reactive")
        return Mono.just(
            GraphQLError.newError()
                .errorType(ErrorType.INTERNAL_ERROR)
                .message(ex.message)
                .build()
        )
    }

}