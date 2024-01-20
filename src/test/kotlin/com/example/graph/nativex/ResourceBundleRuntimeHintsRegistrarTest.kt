package com.example.graph.nativex

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.aot.hint.RuntimeHints
import org.springframework.aot.hint.predicate.RuntimeHintsPredicates

/**
 * @author Julius Krah
 */
class ResourceBundleRuntimeHintsRegistrarTest {

    @Test
    fun `should register resource hints`() {
        val hints = RuntimeHints()
        ResourceBundleRuntimeHintsRegistrar().registerHints(hints, javaClass.classLoader)

        assertThat(RuntimeHintsPredicates.resource().forBundle("messages/messages")).withFailMessage {
            "'messages/message' bundle not registered"
        }.accepts(hints)

        assertThat(RuntimeHintsPredicates.resource().forBundle("ValidationMessages")).withFailMessage {
            "'ValidationMessages' bundle not registered"
        }.accepts(hints)
    }
}
