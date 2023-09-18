package com.example.graph.nativex

import org.springframework.aot.hint.RuntimeHints
import org.springframework.aot.hint.RuntimeHintsRegistrar

/**
 * @author Julius Krah
 */
class ResourceBundleRuntimeHints : RuntimeHintsRegistrar {

    override fun registerHints(hints: RuntimeHints, classLoader: ClassLoader?) {
        hints.resources().registerResourceBundle("sun.util.resources.LocaleNames")
            .registerResourceBundle("ValidationMessages")
            .registerPattern("messages/*")
    }
}