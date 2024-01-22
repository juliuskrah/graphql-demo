package com.example.graph.spring

import io.mongock.runner.springboot.base.config.MongockSpringConfiguration
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.ComponentScan.Filter
import org.springframework.context.annotation.Import
import java.lang.annotation.Inherited
import kotlin.annotation.AnnotationRetention.RUNTIME
import kotlin.annotation.AnnotationTarget.CLASS
import kotlin.reflect.KClass

/**
 * @author Julius Krah
 */
@Target(CLASS)
@Retention(RUNTIME)
@Inherited
@MustBeDocumented
@Import(MongockBeanDefinitionRegistrar::class)
@EnableConfigurationProperties(value = [MongockSpringConfiguration::class])
@ConditionalOnProperty(value = ["\${mongock.enabled}"], havingValue = "true", matchIfMissing = true)
annotation class EnableMongockChangeUnit(
    val basePackages: Array<String> = [],
    val basePackageClasses: Array<KClass<*>> = [],
    val includeFilters: Array<Filter> = [],
    val excludeFilters: Array<Filter> = []
)
