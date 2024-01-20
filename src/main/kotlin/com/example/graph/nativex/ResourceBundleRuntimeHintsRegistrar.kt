package com.example.graph.nativex

import org.springframework.aot.hint.RuntimeHints
import org.springframework.aot.hint.RuntimeHintsRegistrar

/**
 * @author Julius Krah
 */
class ResourceBundleRuntimeHintsRegistrar : RuntimeHintsRegistrar {

    override fun registerHints(hints: RuntimeHints, classLoader: ClassLoader?) {
        hints.resources().registerResourceBundle("sun.util.resources.LocaleNames")
            .registerResourceBundle("ValidationMessages")
            .registerResourceBundle("messages/messages")
    }
}