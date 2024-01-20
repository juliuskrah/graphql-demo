package com.example.graph.nativex

import org.springframework.beans.factory.aot.BeanFactoryInitializationAotContribution
import org.springframework.beans.factory.aot.BeanFactoryInitializationAotProcessor
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory
import org.springframework.core.env.StandardEnvironment
import java.lang.System.Logger.Level.INFO

/**
 * @author Julius Krah
 */
class ChangeUnitBeanFactoryInitializationAotProcessor: BeanFactoryInitializationAotProcessor {
    private val log: System.Logger = System.getLogger(ChangeUnitBeanFactoryInitializationAotProcessor::class.java.canonicalName)

    override fun processAheadOfTime(beanFactory: ConfigurableListableBeanFactory): BeanFactoryInitializationAotContribution {
        val env = StandardEnvironment()
        val packages = env.getProperty("mongock.migration-scan-package", List::class.java)
        log.log(INFO, "Loading ChangeUnits from {0}", packages)
        return BeanFactoryInitializationAotContribution { context, init ->
            context.runtimeHints
            init.methods
            context.runtimeHints.reflection()
        }
    }
}