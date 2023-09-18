package com.example.graph.nativex

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.aot.hint.RuntimeHints
import org.springframework.aot.hint.predicate.RuntimeHintsPredicates

/**
 * @author Julius Krah
 */
class ResourceBundleRuntimeHintsTest {

    @Test
    fun `should register resource hints`() {
        val hints = RuntimeHints()
        ResourceBundleRuntimeHints().registerHints(hints, javaClass.classLoader)

        assertThat(RuntimeHintsPredicates.resource().forResource("messages/messages.properties")).withFailMessage {
            "'messages/message.properties' resource not registered"
        }.accepts(hints)
        assertThat(RuntimeHintsPredicates.resource().forResource("messages/messages_de.properties")).withFailMessage {
            "'messages/message_de.properties' resource not registered"
        }.accepts(hints)

        assertThat(RuntimeHintsPredicates.resource().forBundle("ValidationMessages")).withFailMessage {
            "'ValidationMessages' bundle not registered"
        }.accepts(hints)
    }
}
