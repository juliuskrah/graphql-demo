package com.example.graph.migration

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import com.mongodb.client.model.CreateCollectionOptions
import com.mongodb.client.model.Filters
import com.mongodb.client.model.ValidationOptions
import com.mongodb.client.result.DeleteResult
import com.mongodb.client.result.InsertManyResult
import com.mongodb.reactivestreams.client.ClientSession
import com.mongodb.reactivestreams.client.MongoDatabase
import io.mongock.api.annotations.BeforeExecution
import io.mongock.api.annotations.ChangeUnit
import io.mongock.api.annotations.Execution
import io.mongock.api.annotations.RollbackBeforeExecution
import io.mongock.api.annotations.RollbackExecution
import io.mongock.driver.mongodb.reactive.util.MongoSubscriberSync
import org.bson.Document
import org.bson.conversions.Bson
import org.springframework.aot.hint.annotation.Reflective
import org.springframework.core.io.ClassPathResource
import java.io.IOException
import java.time.Instant
import java.util.ResourceBundle

/**
 * Change Unit for the product entity. Follows the format `yyyyMMddHHmm{action}{name}CollectionChnageUnit`
 * @author Julius Krah
 */
@Reflective
@ChangeUnit(id = "create-product-collection-202401151530", order = "202401151530", author = "Julius Krah")
class `202401151530CreateProductCollectionChangeUnit`(
    private val database: MongoDatabase,
    private val session: ClientSession,
    private val jackson: ObjectMapper
) {
    private val log: System.Logger = System.getLogger(`202401151530CreateProductCollectionChangeUnit`::class.simpleName, ResourceBundle.getBundle("messages.messages"))

    companion object {
        @JvmStatic
        val PRODUCT_COLLECTION_NAME = "products"
    }

    /**
     * ```
     * db.createCollection("products", {
     *   validator: {
     *     $jsonSchema: {
     *       bsonType: "object",
     *       title: "Product Object validation",
     *       required: ["createdAt", "updatedAt", "createdBy", "updatedBy", "title"]
     *       properties: {
     *         title: {
     *           bsonType: "string",
     *           description: "'title' must be a string and is required"
     *         },
     *         createdAt: {
     *           bsonType: "date",
     *           description: "'createdAt' must be a date and is required"
     *         },
     *         updatedAt: {
     *           bsonType: "date",
     *           description: "'updatedAt' must be a date and is required"
     *         },
     *         createdBy: {
     *           bsonType: "string",
     *           description: "'createdBy' must be a string and is required"
     *         },
     *         updatedBy: {
     *           bsonType: "string",
     *           description: "'updatedBy' must be a string and is required"
     *         },
     *         mediaUrls: {
     *           bsonType: "array",
     *           items: {
     *             bsonType: "string"
     *           }
     *         }
     *       }
     *     }
     *   }
     * })
     * ```
     */
    @BeforeExecution
    fun createProductCollection() {
        val bsonType = "bsonType"
        val description = "description"
        val schema: Bson = Document(mapOf(
            bsonType to "object",
            "title" to "Product Object validation",
            "required" to setOf("createdAt", "updatedAt", "createdBy", "updatedBy", "title"),
            "properties" to Document("title", Document(bsonType, "string").append(description, "'title' must be a string and is required"))
                .append("createdAt", Document(bsonType, "date").append(description, "'createdAt' must be a date and is required"))
                .append("updatedAt", Document(bsonType, "date").append(description, "'updatedAt' must be a date and is required"))
                .append("createdBy", Document(bsonType, "string").append(description, "'createdBy' must be a string and is required"))
                .append("updatedBy", Document(bsonType, "string").append(description, "'updatedBy' must be a string and is required"))
                .append("mediaUrls", Document(bsonType, "array").append("items", Document(bsonType, "string")))
        ))
        val validator: Bson = Filters.jsonSchema(schema)
        val validationOptions = ValidationOptions().validator(validator)
        val options = CreateCollectionOptions().validationOptions(validationOptions)
        val subscriber = MongoSubscriberSync<Void>()
        this.database.createCollection(this.session, PRODUCT_COLLECTION_NAME, options).subscribe(subscriber)
        subscriber.await()
    }

    @RollbackBeforeExecution
    fun deleteProductCollection() {
        val subscriber = MongoSubscriberSync<Void>()
        this.database.getCollection(PRODUCT_COLLECTION_NAME).drop(this.session).subscribe(subscriber)
        subscriber.await()
    }

    @Execution
    fun addDataProductCollection() {
        val jsonProducts = readFile("data/products.json")
        log.log(System.Logger.Level.INFO, "log.migration.json.data", jsonProducts)
        val products = jsonProducts.map{
            it["createdAt"] = Instant.ofEpochSecond((it["createdAt"] as Int).toLong())
            it["updatedAt"] = Instant.ofEpochSecond((it["updatedAt"] as Int).toLong())
            it
        }.map(::Document)
        val subscriber = MongoSubscriberSync<InsertManyResult>()
        this.database.getCollection(PRODUCT_COLLECTION_NAME).insertMany(this.session, products).subscribe(subscriber)
        subscriber.await()
    }

    @RollbackExecution
    fun removeDataProductCollection() {
        val subscriber = MongoSubscriberSync<DeleteResult>()
        this.database.getCollection(PRODUCT_COLLECTION_NAME).deleteMany(this.session, Filters.empty()).subscribe(subscriber)
        subscriber.await()
    }

    @Throws(IOException::class)
    private fun readFile(filePath: String): List<MutableMap<String, Any>> {
        val inputStream = ClassPathResource(filePath).inputStream
        return this.jackson.readValue(inputStream, object: TypeReference<List<MutableMap<String, Any>>>(){})
    }
}