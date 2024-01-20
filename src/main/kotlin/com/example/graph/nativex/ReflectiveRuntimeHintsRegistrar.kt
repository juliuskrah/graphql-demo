package com.example.graph.nativex

import com.example.graph.migration.`202401151530CreateProductCollectionChangeUnit`
import io.mongock.runner.core.executor.system.changes.SystemChangeUnit00001
import org.springframework.aot.hint.ExecutableMode
import org.springframework.aot.hint.MemberCategory.INTROSPECT_PUBLIC_METHODS
import org.springframework.aot.hint.MemberCategory.INVOKE_DECLARED_CONSTRUCTORS
import org.springframework.aot.hint.MemberCategory.INVOKE_DECLARED_METHODS
import org.springframework.aot.hint.RuntimeHints
import org.springframework.aot.hint.RuntimeHintsRegistrar
import org.springframework.aot.hint.registerType

/**
 * @author Julius Krah
 */
class ReflectiveRuntimeHintsRegistrar : RuntimeHintsRegistrar {

    override fun registerHints(hints: RuntimeHints, classLoader: ClassLoader?) {
        hints.reflection().registerTypeIfPresent(classLoader, "org.springframework.graphql.client.ResponseMapGraphQlResponse\$MapResponseError") { typeHint ->
            typeHint.withMethod("getExtensions", listOf(), ExecutableMode.INVOKE)
                .withMethod("getMessage", listOf(), ExecutableMode.INVOKE)
                .withMethod("getPath", listOf(), ExecutableMode.INVOKE)
                .withMembers(INTROSPECT_PUBLIC_METHODS)
        }
        hints.reflection().registerType<`202401151530CreateProductCollectionChangeUnit`> {
            it.withMembers(INVOKE_DECLARED_METHODS, INVOKE_DECLARED_CONSTRUCTORS)
        }
        hints.reflection().registerType<SystemChangeUnit00001> {
            it.withMembers(INVOKE_DECLARED_METHODS, INVOKE_DECLARED_CONSTRUCTORS)
        }
    }
}