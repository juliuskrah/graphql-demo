package com.example.graph.nativex

import io.mongock.api.annotations.ChangeUnit
import io.mongock.runner.springboot.MongockSpringboot
import org.springframework.aot.hint.MemberCategory.INVOKE_DECLARED_CONSTRUCTORS
import org.springframework.aot.hint.MemberCategory.INVOKE_DECLARED_METHODS
import org.springframework.aot.hint.ReflectionHints
import org.springframework.aot.hint.registerType
import org.springframework.beans.factory.aot.BeanFactoryInitializationAotContribution
import org.springframework.beans.factory.aot.BeanFactoryInitializationAotProcessor
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory
import org.springframework.boot.autoconfigure.AutoConfigurationPackages
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider
import org.springframework.core.type.filter.AnnotationTypeFilter
import org.springframework.util.ClassUtils
import org.springframework.util.StringUtils

/**
 * @author Julius Krah
 */
class ChangeUnitBeanFactoryInitializationAotProcessor: BeanFactoryInitializationAotProcessor {

    override fun processAheadOfTime(beanFactory: ConfigurableListableBeanFactory): BeanFactoryInitializationAotContribution? {
        if (AutoConfigurationPackages.has(beanFactory)) {
            val changeUnitSet = mutableSetOf<Class<*>>()
            val scanner = createScanner()
            AutoConfigurationPackages.get(beanFactory).forEach { packageToScan ->
                if(StringUtils.hasText(packageToScan)) {
                    for(beanDefinition in scanner.findCandidateComponents(packageToScan)) {
                        changeUnitSet.add(ClassUtils.forName(beanDefinition.beanClassName!!, beanFactory.beanClassLoader))
                    }
                }
            }
            return BeanFactoryInitializationAotContribution { context, _ ->
                val hints = context.runtimeHints.reflection()
                registerReflectiveHints(hints, changeUnitSet)
            }
        }
        return null
    }

    private fun createScanner(): ClassPathScanningCandidateComponentProvider {
        val scanner = ClassPathScanningCandidateComponentProvider(false)
        scanner.addIncludeFilter(AnnotationTypeFilter(ChangeUnit::class.java))
        return scanner
    }

    private fun registerReflectiveHints(hints: ReflectionHints, changeUnits: Set<Class<*>>) {
        changeUnits.forEach {
            hints.registerType(it) { hint ->
                hint.withMembers(INVOKE_DECLARED_METHODS, INVOKE_DECLARED_CONSTRUCTORS)
            }
        }
        hints.registerType<MongockSpringboot.RunnerSpringbootBuilderImpl> {hint ->
            hint.withMembers(INVOKE_DECLARED_METHODS, INVOKE_DECLARED_CONSTRUCTORS)
        }
    }
}
