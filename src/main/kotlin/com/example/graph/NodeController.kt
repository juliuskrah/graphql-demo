package com.example.graph

import com.example.graph.generated.types.Node
import com.example.graph.generated.types.Product
import com.example.graph.generated.types.ProductInput
import graphql.GraphQLError
import graphql.schema.DataFetchingEnvironment
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.reactive.asFlow
import kotlinx.coroutines.reactor.awaitSingle
import kotlinx.coroutines.reactor.awaitSingleOrNull
import org.springframework.context.MessageSource
import org.springframework.context.MessageSourceAware
import org.springframework.data.domain.ScrollPosition
import org.springframework.data.domain.Window
import org.springframework.graphql.data.method.annotation.Argument
import org.springframework.graphql.data.method.annotation.GraphQlExceptionHandler
import org.springframework.graphql.data.method.annotation.MutationMapping
import org.springframework.graphql.data.method.annotation.QueryMapping
import org.springframework.graphql.data.query.ScrollSubrange
import org.springframework.graphql.execution.ErrorType
import org.springframework.stereotype.Controller
import java.lang.System.Logger.Level.INFO

/**
 * @author Julius Krah
 */
@Controller
class NodeController (
    private val nodeResolver: NodeResolver,
    private val nodeService: ProductService
) : MessageSourceAware {

    private lateinit var messageSource: MessageSource
    private val log: System.Logger = System.getLogger(NodeController::class.java.canonicalName)

    override fun setMessageSource(messageSource: MessageSource) {
        this.messageSource = messageSource
    }

    @QueryMapping
    suspend fun node(@Argument id: String): Node? {
        log.log(INFO, "Using node id: {0}", id)
        return nodeResolver.nodeById(id).awaitSingleOrNull()
    }

    @QueryMapping
    suspend fun product(@Argument id: String): Product? {
        log.log(INFO, "Using product id: {0}", id)
        return nodeService.product(id).awaitSingleOrNull()
    }

    @MutationMapping
    suspend fun addProduct(@Argument input: ProductInput): Product {
        log.log(INFO, "Saving product: {0}", input)
        return nodeService.product(product = input).awaitSingle()
    }

    @QueryMapping
    fun nodes(@Argument ids: List<String>): Flow<Node> {
        log.log(INFO, "Using node ids: {0}", ids)
        return nodeResolver.nodeByIds(ids).asFlow()
    }

    @QueryMapping
    suspend fun products(subrange: ScrollSubrange): Window<Product> {
        log.log(INFO, "Using forward={0} and count={1} with position={2}",
            subrange.forward(),
            subrange.count().orElse(20),
            subrange.position().orElse(ScrollPosition.offset()))
        TODO()
    }

    @GraphQlExceptionHandler
    suspend fun handle(ex: ProductNotFoundException, env: DataFetchingEnvironment): GraphQLError = coroutineScope {
        log.log(INFO, "Handling `ProductNotFoundException`")
        GraphQLError.newError()
            .errorType(ErrorType.NOT_FOUND)
            .message(messageSource.getMessage(ex.message!!,
                arrayOf(env.getArgument("id")), null, env.locale))
            .path(env.executionStepInfo.path)
            .location(env.field.sourceLocation)
            .build()
    }

    @GraphQlExceptionHandler
    suspend fun handle(ex: IllegalArgumentException, env: DataFetchingEnvironment): GraphQLError = coroutineScope {
        log.log(INFO, "Handling `IllegalArgumentException`", ex)
        GraphQLError.newError()
            .errorType(ErrorType.BAD_REQUEST)
            .message(messageSource.getMessage(ex.message!!,
                arrayOf(env.getArgument("id")), ex.message, env.locale))
            .path(env.executionStepInfo.path)
            .location(env.field.sourceLocation)
            .build()
    }

}