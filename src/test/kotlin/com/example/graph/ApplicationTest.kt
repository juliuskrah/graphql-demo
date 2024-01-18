package com.example.graph

import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest(properties = ["mongock.enabled=false"])
class ApplicationTest {
    @Test
    fun `should load context`() {

    }
}