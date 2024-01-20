package com.example.graph.nativex

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.aot.hint.MemberCategory
import org.springframework.aot.hint.RuntimeHints
import org.springframework.aot.hint.TypeHint
import org.springframework.aot.test.agent.EnabledIfRuntimeHintsAgent
import org.springframework.aot.test.agent.RuntimeHintsRecorder
import org.springframework.util.ClassUtils


/**
 * @author Julius Krah
 */
@EnabledIfRuntimeHintsAgent
class ReflectiveRuntimeHintsRegistrarTest {

    @Test
    fun `should provide hints for reflective types`() {
        val hints = RuntimeHints()
        val klass = "org.springframework.graphql.client.ResponseMapGraphQlResponse\$MapResponseError"
        hints.reflection().registerTypeIfPresent(javaClass.classLoader, klass) { hint: TypeHint.Builder ->
            hint.withMembers(MemberCategory.INTROSPECT_PUBLIC_METHODS)
        }
        val invocations = RuntimeHintsRecorder.record {
            ClassUtils.forName(klass, javaClass.classLoader).methods
        }
        assertThat(invocations).match(hints)
    }
}