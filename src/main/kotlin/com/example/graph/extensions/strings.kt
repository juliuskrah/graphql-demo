@file:OptIn(ExperimentalEncodingApi::class)

package com.example.graph.extensions

import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi

/**
 * Encodes string to base64
 * @receiver String
 */
fun String.toBase64(): String {
  return if (this.isBlank()) this
  else {
      val base64 = Base64.Default
      base64.encode(this.toByteArray())
  }
}

/**
 * Decode string from base64
 * @receiver String
 */
fun String.fromBase64(): String {
    return if (this.isBlank()) this
    else {
        val base64 = Base64.Default
        return base64.decode(this).toString(Charsets.UTF_8)
    }
}
