package com.example.graph

import com.example.graph.generated.DgsConstants
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.Test

/**
 * @author Julius Krah
 */
class GlobalIdTest {

    @Test
    fun `should not create nodeId with invalid base64 padding`() {
        val base64String = "==qwerty=="
        assertThatThrownBy {
            GlobalId.from(base64String)
        }.isInstanceOf(IllegalArgumentException::class.java)
            .hasMessage("Redundant pad character at index 0")
    }

    @Test
    fun `should not create nodeId with invalid gid`() {
        // Product:cd8137c1-03f4-4663-8638-5afb727f6029
        val base64String = "UHJvZHVjdDpjZDgxMzdjMS0wM2Y0LTQ2NjMtODYzOC01YWZiNzI3ZjYwMjkK"
        assertThatThrownBy {
            GlobalId.from(base64String)
        }.isInstanceOf(IllegalArgumentException::class.java)
            .hasMessage("Invalid ID: $base64String")
    }

    @Test
    fun `should create nodeId from base64`() {
        // gid://demo/Product/55ec5d24-4ca6-432f-a303-f2f181174c19
        val base64String = "Z2lkOi8vZGVtby9Qcm9kdWN0LzU1ZWM1ZDI0LTRjYTYtNDMyZi1hMzAzLWYyZjE4MTE3NGMxOQ"
        val nodeId = GlobalId.from(base64String)
        assertThat(nodeId).isNotNull()
            .extracting(GlobalId::node).isEqualTo("Product")
        assertThat(nodeId)
            .extracting(GlobalId::id).isEqualTo("55ec5d24-4ca6-432f-a303-f2f181174c19")
    }

    @Test
    fun `should convert to base64`() {
        val nodeId = GlobalId(DgsConstants.PRODUCT.TYPE_NAME, "55ec5d24-4ca6-432f-a303-f2f181174c19")
        // gid://demo/Product/55ec5d24-4ca6-432f-a303-f2f181174c19
        val base64String = "Z2lkOi8vZGVtby9Qcm9kdWN0LzU1ZWM1ZDI0LTRjYTYtNDMyZi1hMzAzLWYyZjE4MTE3NGMxOQ=="
        assertThat(nodeId.toBase64()).isBase64().isEqualTo(base64String)
    }

    @Test
    fun `should convert to gid`() {
        val nodeId = GlobalId(DgsConstants.PRODUCT.TYPE_NAME, "55ec5d24-4ca6-432f-a303-f2f181174c19")
        assertThat(nodeId.toString()).isEqualTo("gid://demo/Product/55ec5d24-4ca6-432f-a303-f2f181174c19")
    }
}
