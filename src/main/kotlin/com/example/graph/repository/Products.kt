package com.example.graph.repository

import org.springframework.data.mongodb.repository.ReactiveMongoRepository
import java.util.UUID

/**
 * @author Julius Krah
 */
interface Products: ReactiveMongoRepository<ProductEntity, String> {
}