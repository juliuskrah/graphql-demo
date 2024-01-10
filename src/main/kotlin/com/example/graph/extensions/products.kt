package com.example.graph.extensions

import com.example.graph.repository.ProductEntity
import com.example.graph.generated.types.Product
import com.example.graph.generated.types.ProductInput

fun ProductEntity.toProduct() = Product(
    id = id!!,
    title = title,
    description = description,
    mediaUrl = mediaUrls
)

fun ProductInput.toProductEntity() = ProductEntity(
    title = title,
    description = description,
    mediaUrls = mediaUrl
)
