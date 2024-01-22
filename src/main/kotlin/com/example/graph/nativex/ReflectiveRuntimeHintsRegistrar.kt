package com.example.graph.nativex

import org.springframework.aot.hint.ExecutableMode
import org.springframework.aot.hint.MemberCategory.INTROSPECT_PUBLIC_METHODS
import org.springframework.aot.hint.RuntimeHints
import org.springframework.aot.hint.RuntimeHintsRegistrar

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
    }
}