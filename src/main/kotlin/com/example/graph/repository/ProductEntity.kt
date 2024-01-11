package com.example.graph.repository

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import java.net.URL
import java.util.UUID

/**
 * @author Julius Krah
 */
@Document(collection = "products")
data class ProductEntity(
    @Id val id: String? = null,
    val title: String? = null,
    val description: String? = null,
    val mediaUrls: List<URL?>? = null
)
