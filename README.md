# GraphQL with Spring Boot

This repository contains a sample project for `GraphQL` using `Spring Boot`.

## Schema

The schema for this example.

```graphql
type Query {
    node(id: ID!): Node
    nodes(ids: [ID!]!): [Node]!
    product(id: ID!): Product
    products(first: Int, after: String, last: Int, before: String): ProductConnection
}

interface Node {
  "Global ID: (gid://demo/{node}/{id}) encoded as base64 string"
  id: ID!
}

type Mutation {
    addProduct(input: ProductInput!): Product
}

type Product implements Node {
    id: ID!
    title: String
    description: String
    createdAt: DateTime
    updatedAt: DateTime
    mediaUrl: [URL]
}

input ProductInput {
    title: String!
    description: String
    mediaUrl: [URL]
}

schema {
    query: Query
    mutation: Mutation
}

scalar URL
scalar DateTime
```

## Using Global ID

The Global Identifier for `Node` types are represented as `gid://demo/{node}/{id}` base64 decoded.

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

## Take it for a spin

You need Java 21 to run this demo:

```bash
./gradlew bootRun
```

Endpoint: http://localhost:8080/graphql

Login (using the users below) and grab an `access_token`:

```graphql
mutation userLogin($username: String!, $password: String!) {
    login(username: $username, password: $password) {
        access_token
        expires_in
    }
}
```

Users:

| Username | Password | Roles         |
|----------|----------|---------------|
| user     | password | product:read  |
| admin    | password | product:write |

Use the access token in the response for subsequent requests. Add a header in the form `Authorization: Bearer <token>`

Create a Product:

```graphql
mutation addProduct($title: String!, $description: String, $mediaUrl: [URL]) {
    addProduct(input: {title: $title, description: $description, mediaUrl: $mediaUrl}) {
        id
        title
        description
        mediaUrl
        createdAt
        updatedAt
    }
}
```

Grab the `id` in the response for the next request.

Find a product by its global `id`:

```graphql
query productById($productId: ID!) {
    product(id: $productId) {
        id
        title
        description
        mediaUrl
        createdAt
        updatedAt
    }
}
```

> :information_source: You can find more examples in the [`src/main/resources/graphql`](src/main/resources/graphql) directory.

### Building a native binary

You need a [Native Image Kit](https://bell-sw.com/pages/downloads/native-image-kit/#/nik-22-17) v22.3 and above.

```bash
./gradlew nativeCompile
```

Set up the following environment variables:

```bash
export SPRING_DATA_MONGODB_DATABASE=test
export SPRING_DATA_MONGODB_USERNAME=testuser
export SPRING_DATA_MONGODB_PASSWORD=testpassword
export SPRING_DATA_MONGODB_AUTHENTICATION_DATABASE=admin
```

Start the database:

```bash
docker compose -f src/main/resources/compose.yml -f src/main/resources/compose.dev.yml up -d
````

The native image executable can be found the `build/native/nativeCompile`

```bash
./build/native/nativeCompile/graphql-demo
```

TODO

- [ ] File upload
- [ ] Queries
  - [ ] sorting
  - [ ] filters
- [x] Migration
  - [ ] aot processing
- [x] Authentication
  - [ ] Authorization
- [x] Paging with keyset
- [x] Fix tests
- [x] Database migration
