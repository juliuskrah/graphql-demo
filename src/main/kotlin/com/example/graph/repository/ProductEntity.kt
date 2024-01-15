package com.example.graph.repository

import org.springframework.data.annotation.CreatedBy
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.Id
import org.springframework.data.annotation.LastModifiedBy
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.mongodb.core.mapping.Document
import java.net.URL
import java.time.Instant
import java.util.UUID

/**
 * @author Julius Krah
 */
@Document(collection = "products")
data class ProductEntity(
    @Id val id: String? = null,
    val title: String? = null,
    val description: String? = null,
    val mediaUrls: List<URL?>? = null,
    @CreatedDate
    val createdAt: Instant? = null,
    @LastModifiedDate
    val updatedAt: Instant? = null,
    @CreatedBy
    val createdBy: String? = null,
    @LastModifiedBy
    val updatedBy: String? = null
)
