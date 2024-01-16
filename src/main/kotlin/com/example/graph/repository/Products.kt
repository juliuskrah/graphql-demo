package com.example.graph.repository

import org.springframework.data.mongodb.repository.ReactiveMongoRepository

/**
 * @author Julius Krah
 */
interface Products: ReactiveMongoRepository<ProductEntity, String>
