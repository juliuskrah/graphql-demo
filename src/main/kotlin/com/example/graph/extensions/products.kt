package com.example.graph.extensions

import com.example.graph.repository.ProductEntity
import com.example.graph.generated.types.Product
import com.example.graph.generated.types.ProductInput
import java.time.ZoneOffset.UTC

fun ProductEntity.toProduct() = Product(
    id = id!!,
    title = title!!,
    description = description,
    mediaUrl = mediaUrls?.map { it?.toURL() },
    createdAt = createdAt!!.atOffset(UTC),
    updatedAt = updatedAt!!.atOffset(UTC)
)

fun ProductInput.toProductEntity() = ProductEntity(
    title = title,
    description = description,
    mediaUrls = mediaUrl?.map { it?.toURI() }
)
