package com.example.graph

import com.example.graph.extensions.toProduct
import com.example.graph.extensions.toProductEntity
import com.example.graph.generated.DgsConstants
import com.example.graph.generated.types.Product
import com.example.graph.generated.types.ProductInput
import com.example.graph.repository.Products
import org.springframework.context.MessageSource
import org.springframework.context.MessageSourceAware
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.toMono
import java.util.Locale
import java.lang.System.Logger.Level.INFO

/**
 * @author Julius Krah
 */
@Service(DgsConstants.PRODUCT.TYPE_NAME)
class ProductServiceImpl(private val productRepository: Products)
    : ProductService, MessageSourceAware {
    private val log: System.Logger = System.getLogger(ProductServiceImpl::class.java.canonicalName)
    private val products = listOf(
        Product("5f26acfd-8ace-4321-a426-ab9fb983fdfe", "product.service.cell-phone.title"),
        Product("a79a3638-b0f4-468a-a688-11f325ada890", "product.service.smart-watch.title"),
        Product("571a2376-24b6-4b05-8c90-7d38cd041bfd", "product.service.head-phone.title"),
        Product("71e89777-24fb-4090-8a27-14756dd69b71", "product.service.phone-case.title"),
        Product("f5c1287c-0bc5-4b05-8af4-5fe7d3cbc99e", "product.service.phone-sleeve.title"),
        Product("71006afe-1d07-460b-a7c7-da4cd7905fe0", "product.service.calculator.title"),
    )

    private lateinit var messageSource: MessageSource

    private fun mapWithGlobalId(product: Product, locale: Locale): Product {
        log.log(INFO, "Product id: {0}", product.id)
        val globalId = GlobalId(DgsConstants.PRODUCT.TYPE_NAME, product.id)
        return product.copy(id = globalId.toBase64(), title =
            messageSource.getMessage(product.title!!, null, locale))
    }

    private fun mapWithGlobalId(product: Product): Product {
        log.log(INFO, "Product id: {0}", product.id)
        val globalId = GlobalId(DgsConstants.PRODUCT.TYPE_NAME, product.id)
        return product.copy(id = globalId.toBase64())
    }

    override fun setMessageSource(messageSource: MessageSource) {
        this.messageSource = messageSource
    }

    override fun product(id: String): Mono<Product> {
        return Mono.deferContextual { contextView ->
            val locale: Locale = contextView.getOrDefault(Locale::class, Locale.getDefault())!!
            val (_, decodedId) = GlobalId.from(id).ensureNode(DgsConstants.PRODUCT.TYPE_NAME)
            products.find { it.id == decodedId }
                .toMono()
                .map{ mapWithGlobalId(it, locale) }
                .switchIfEmpty(Mono.error(ProductNotFoundException("exception.product.service.not-found")))
        }
    }

    override fun product(product: ProductInput): Mono<Product> {
        return productRepository.save(product.toProductEntity()).map { mapWithGlobalId(it.toProduct()) }
    }

    override fun node(id: String): Mono<Product> = product(id)

    override fun nodes(ids: List<String>): Flux<Product> {
//        return ids.asSequence().map {id: String -> GlobalId.from(id).id }
//            .map { id -> products.find { id == it.id }}
//            .asStream()
        TODO("Not implemented yet")
    }
}