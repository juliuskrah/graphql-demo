package com.example.graph.spring

import com.example.graph.repository.MongockRunnerSupport
import io.mongock.runner.core.executor.MongockRunner
import io.mongock.runner.core.executor.MongockRunnerImpl
import org.springframework.beans.factory.config.BeanDefinition
import org.springframework.beans.factory.support.BeanDefinitionBuilder
import org.springframework.beans.factory.support.BeanDefinitionRegistry
import org.springframework.beans.factory.support.BeanNameGenerator
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider
import org.springframework.context.annotation.ConfigurationClassPostProcessor
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar
import org.springframework.context.annotation.TypeFilterUtils
import org.springframework.core.annotation.AnnotationAttributes
import org.springframework.core.env.Environment
import org.springframework.core.io.ResourceLoader
import org.springframework.core.type.AnnotationMetadata
import org.springframework.core.type.filter.TypeFilter
import org.springframework.util.ClassUtils
import java.beans.Introspector
import java.util.stream.Stream

/**
 * @author Julius Krah
 */
class MongockBeanDefinitionRegistrar(
    private val environment: Environment,
    private val resourceLoader: ResourceLoader
): ImportBeanDefinitionRegistrar {

    override fun registerBeanDefinitions(metadata: AnnotationMetadata, registry: BeanDefinitionRegistry) {
        registerBeanDefinitions(metadata, registry, ConfigurationClassPostProcessor.IMPORT_BEAN_NAME_GENERATOR)
    }

    override fun registerBeanDefinitions(
        metadata: AnnotationMetadata,
        registry: BeanDefinitionRegistry,
        importBeanNameGenerator: BeanNameGenerator,
    ) {
        val annotation: Class<out Annotation> = EnableMongockChangeUnit::class.java
        val attributes = metadata.getAnnotationAttributes(annotation.name)
            ?: throw IllegalStateException("Unable to obtain annotation attributes for $annotation")
        val annotationAttributes = AnnotationAttributes(attributes)
        val packages = getBasePackages(annotationAttributes, metadata)
        val includeFilters = getIncludeFilters(annotationAttributes, registry)
        val excludeFilters = getExcludeFilters(annotationAttributes, registry)
        val changeUnitSets = scanner(packages, includeFilters, excludeFilters).map {
            ClassUtils.forName(it.beanClassName!!, resourceLoader.classLoader)
        }

        val mongockSupportBeanDefinitionBuilder = BeanDefinitionBuilder
            .rootBeanDefinition(MongockRunnerSupport::class.java)
            .addPropertyValue("migrationClasses", changeUnitSets)
            .addPropertyReference("driver", "connectionDriver")
            .addPropertyReference("config", "mongock-io.mongock.runner.springboot.base.config.MongockSpringConfiguration")

        val mongockRunnerBeanDefinitionBuilder = BeanDefinitionBuilder.rootBeanDefinition(MongockRunnerImpl::class.java)
            .setFactoryMethodOnBean("create", "mongockRunnerSupport")

        registry.registerBeanDefinition(getName(MongockRunner::class.java), mongockRunnerBeanDefinitionBuilder.beanDefinition)
        registry.registerBeanDefinition("mongockRunnerSupport", mongockSupportBeanDefinitionBuilder.beanDefinition)
    }

    private fun getBasePackages(attributes: AnnotationAttributes, metadata: AnnotationMetadata): Stream<String> {
        val basePackages = attributes.getStringArray("basePackages")
        val basePackageClasses = attributes.getClassArray("basePackageClasses")
        if (basePackages.isEmpty() && basePackageClasses.isEmpty()) {
            val className: String = metadata.className
            return Stream.of(ClassUtils.getPackageName(className))
        }

        val packages: MutableSet<String> = HashSet()
        packages.addAll(listOf(*basePackages))

        for (c in basePackageClasses) {
            packages.add(ClassUtils.getPackageName(c))
        }
        return Stream.of(packages).flatMap(Collection<String>::stream)
    }

    private fun scanner(packages: Stream<String>, includeFilters: Stream<TypeFilter>,
                        excludeFilters: Stream<TypeFilter>): List<BeanDefinition> {
        val scanner = ClassPathScanningCandidateComponentProvider(false)
        includeFilters.forEach(scanner::addIncludeFilter)
        excludeFilters.forEach(scanner::addExcludeFilter)
        return packages.map(scanner::findCandidateComponents)
            .flatMap(Collection<BeanDefinition>::stream).toList()
    }

    private fun parseFilters(attributeName: String, attributes: AnnotationAttributes,
                             registry: BeanDefinitionRegistry): Stream<TypeFilter> {
        val filters: Array<AnnotationAttributes> = attributes.getAnnotationArray(attributeName)
        val typeFilters = filters.flatMap {
            TypeFilterUtils.createTypeFiltersFor(it, this.environment, this.resourceLoader, registry)
        }
        return typeFilters.stream()
    }

    private fun getIncludeFilters(attributes: AnnotationAttributes, registry: BeanDefinitionRegistry): Stream<TypeFilter> {
        return parseFilters("includeFilters", attributes, registry)
    }

    private fun getExcludeFilters(attributes: AnnotationAttributes, registry: BeanDefinitionRegistry): Stream<TypeFilter> {
        return parseFilters("excludeFilters", attributes, registry)
    }

    private fun getName(clazz: Class<*>) = Introspector.decapitalize(clazz.simpleName)

}