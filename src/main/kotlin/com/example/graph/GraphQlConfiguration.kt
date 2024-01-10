package com.example.graph

import graphql.Scalars
import graphql.scalars.ExtendedScalars
import graphql.schema.idl.RuntimeWiring
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.graphql.execution.RuntimeWiringConfigurer

/**
 * @author Julius Krah
 */
@Configuration(proxyBeanMethods = false)
class GraphQlConfiguration {

    @Bean
    fun runtimeWiring() = RuntimeWiringConfigurer { builder ->
        builder.scalar(
                ExtendedScalars.newAliasedScalar("URL")
                .aliasedScalar(ExtendedScalars.Url)
                .build())
    }
}