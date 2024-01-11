package com.example.graph.support

import com.example.graph.extensions.fromBase64
import com.example.graph.extensions.toBase64

/**
 * @author Julius Krah
 */
data class GlobalId(val node: String, val id: String) {
    /**
     * Decoded string is of the following format:
     * `gid://demo/{node}/{id}`.
     * @return base64 string
     */
    fun toBase64(): String {
        return toGid().toBase64()
    }

    /**
     * Ensure the node given by the client matches the node for the current field.
     * This will prevent a gid `gid://demo/Order/123456789` of an Order node from
     * matching a gid `gid://demo/Product/123456789` of a Product.
     *
     * Given
     * ```
     * query {
     *   product(id: "gid://demo/Order/123456789") {
     *    ...
     *   }
     * }
     * ```
     *
     * Then <br/>
     * we query the Product database with ID `123456788` which may return a result, however we used a GID
     * for `Order`
     */
    fun ensureNode(node: String): GlobalId {
        return if (this.node != node) throw IllegalArgumentException("Invalid node `${this.node}` expected `$node`")
        else this
    }

    override fun toString(): String {
        return toGid()
    }


    private fun toGid(): String {
        val gid = "gid://demo/{node}/{id}"
        val regex = """\{(.+?)}""".toRegex()
        val tokens = mapOf(
            "node" to node,
            "id" to id
        )
        return gid.replace(regex) {
            tokens.getOrDefault(it.groupValues[1], it.value)
        }
    }

    companion object {

        /**
         * Decoded base64 string is expected to be of the following format:
         * `gid://demo/{node}/{id}`.
         *
         * @throws IllegalArgumentException when the base64 string is invalid or is not of the required format
         * @since 1.0.0
         * @return [GlobalId]
         */
        @Throws(IllegalArgumentException::class)
        fun from(base64: String): GlobalId {
            val decoded = base64.fromBase64()
            val regex = """^gid://demo/([A-Z][a-zA-Z]*)/([a-z0-9]+(-[a-z0-9]+)*)$""".toRegex()
            val matchResult = regex.find(decoded)
            val (node, id) = matchResult?.destructured ?: throw IllegalArgumentException("Invalid ID: $base64")
            return GlobalId(node, id)
        }
    }

}
