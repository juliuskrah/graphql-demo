# GraphQL with Spring Boot

This repository contains a sample project for `GraphQL` using `Spring Boot`.

## Schema

```graphql
type Query {
  node(id: ID!): Node
  nodes(ids: [ID!]!): [Node]!
  product(id: ID!): Product
}

interface Node {
  "Global ID: (gid://demo/{node}/{id}) encoded as base64 string"
  id: ID!
}

type Product implements Node{
  id: ID!
  title: String
  description: String
}

schema {
  query: Query
}
```

## Global ID

The Global Identifier for `Node` types are represented as `gid://demo/{node}/{id}` when decoded from base64.

Given a Global ID of

- `Z2lkOi8vZGVtby9Qcm9kdWN0LzU1ZWM1ZDI0LTRjYTYtNDMyZi1hMzAzLWYyZjE4MTE3NGMxOQ==`

this will be decoded as

- `gid://demo/Product/55ec5d24-4ca6-432f-a303-f2f181174c19`

This can be represented as a Kotlin type `GlobalId` with methods to (encode | decode) from base64.

```kotlin
data class GlobalId(val node: String, val id: String) {

    fun toBase64(): String {
        return toGid().toBase64()
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

```

## Localization

Localization is implemented by relying on the `Accept-Language` header of the HTTP request. Take the
following request for example:

```bash
curl --location 'http://localhost:8080/graphql' \
--header 'Accept-Language: de-DE' \
--header 'Content-Type: application/json' \
--data '{"query":"query productNodeDetails($id: ID!) {\n    node(id: $id) {\n        id\n        __typename\n        ... on Product {\n            title\n            description\n        }\n    }\n}\n","variables":{"id":"Z2lkOi8vZGVtby9Qcm9kdWN0LzcxZTg5Nzc3LTI0ZmItNDA5MC04YTI3LTE0NzU2ZGQ2OWI3MQ"}}'
```

The language is set to German using `--header 'Accept-Language: de-DE'`.

This is achieved by setting the `locale` via a `org.springframework.graphql.server.WebGraphQlInterceptor`:

```kotlin
@Service
class LocalePopulatingInterceptor: WebGraphQlInterceptor {
    override fun intercept(request: WebGraphQlRequest, chain: WebGraphQlInterceptor.Chain): Mono<WebGraphQlResponse> {
        val map = mapOf<Any, Any>(
            Locale::class to request.locale
        )
        return chain.next(request).contextWrite{ ctx -> ctx.putAllMap(map) }
    }
}
```

We receive this in the `ProductService`:

```kotlin
@Service(DgsConstants.PRODUCT.TYPE_NAME)
class ProductService: MessageSourceAware {
    
    private lateinit var messageSource: MessageSource
    
    override fun setMessageSource(messageSource: MessageSource) {
        this.messageSource = messageSource
    }

    private fun translate(product: Product, locale: Locale): Product {
        val translated = messageSource.getMessage(product.title!!, null, locale)
        // ...
    }

    fun product(id: String): Mono<Product> {
        return Mono.deferContextual { contextView ->
            val locale: Locale = contextView.getOrDefault(Locale::class, Locale.getDefault())!!
            val product: Product = ...
            translate(product, locale)
            // ...
        }
    }
}
```

## Take it for a spin

You need Java 17 to run this:

```bash
./gradlew bootRun
```

Endpoint: http://localhost:8080

Request:

```graphql
{
    node(id: "Z2lkOi8vZGVtby9Qcm9kdWN0LzcxZTg5Nzc3LTI0ZmItNDA5MC04YTI3LTE0NzU2ZGQ2OWI3MQ") {
        id
        __typename
        ... on Product {
            title
            description
        }
    }
}
```

### Building a native binary

You need a [Native Image Kit](https://bell-sw.com/pages/downloads/native-image-kit/#/nik-22-17) v22.3 and above.

```bash
./gradlew nativeCompile
```

The native image executable can be found the `build/native/nativeCompile`

```bash
./build/native/nativeCompile/graphql-demo
```

TODO

- File upload
- Authentication
- Paging with keyset
