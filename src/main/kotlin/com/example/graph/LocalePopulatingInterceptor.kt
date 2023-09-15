package com.example.graph

import org.springframework.graphql.server.WebGraphQlInterceptor
import org.springframework.graphql.server.WebGraphQlRequest
import org.springframework.graphql.server.WebGraphQlResponse
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono
import java.util.Locale

/**
 * @author Julius Krah
 */
@Service
class LocalePopulatingInterceptor: WebGraphQlInterceptor {
    override fun intercept(request: WebGraphQlRequest, chain: WebGraphQlInterceptor.Chain): Mono<WebGraphQlResponse> {
        val map = mapOf<Any, Any>(
            Locale::class to request.locale
        )
        return chain.next(request).contextWrite{ ctx -> ctx.putAllMap(map) }
    }
}