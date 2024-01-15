package com.example.graph

import com.example.graph.extensions.toProduct
import com.example.graph.extensions.toProductEntity
import com.example.graph.generated.DgsConstants
import com.example.graph.generated.types.Product
import com.example.graph.generated.types.ProductInput
import com.example.graph.repository.ProductEntity
import com.example.graph.repository.Products
import com.example.graph.support.ProductNotFoundException
import org.springframework.context.MessageSource
import org.springframework.context.MessageSourceAware
import org.springframework.data.domain.Example
import org.springframework.data.domain.ScrollPosition
import org.springframework.data.domain.Window
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.lang.System.Logger.Level.INFO
import java.util.Locale

/**
 * @author Julius Krah
 */
@Transactional
@Service(DgsConstants.PRODUCT.TYPE_NAME)
class ProductServiceImpl(private val productRepository: Products)
    : ProductService, MessageSourceAware {
    private val log: System.Logger = System.getLogger(ProductServiceImpl::class.java.canonicalName)
    private lateinit var messageSource: MessageSource

    private fun mapWithGlobalId(product: Product, locale: Locale): Product {
        log.log(INFO, "Product id: {0}", product.id)
        return product.copy(id = product.id, title =
            messageSource.getMessage(product.title, null, locale))
    }

    override fun setMessageSource(messageSource: MessageSource) {
        this.messageSource = messageSource
    }

    @Transactional(readOnly = true)
    override fun product(id: String): Mono<Product> {
        return this.productRepository.findById(id)
            .map { it.toProduct() }
            .switchIfEmpty(Mono.error(ProductNotFoundException("exception.product.service.not-found")))
    }

    override fun product(product: ProductInput): Mono<Product> {
        return this.productRepository.save(product.toProductEntity()).map { it.toProduct() }
    }

    override fun products(count: Int, scroll: ScrollPosition): Mono<Window<Product>> {
        return this.productRepository.findBy(Example.of(ProductEntity())) { query ->
            query.`as`(Product::class.java).limit(count).scroll(scroll)
        }
    }

    override fun node(id: String): Mono<Product> = product(id)

    override fun nodes(ids: List<String>): Flux<Product> {
//        return ids.asSequence().map {id: String -> GlobalId.from(id).id }
//            .map { id -> products.find { id == it.id }}
//            .asStream()
        TODO("Not implemented yet")
    }
}
