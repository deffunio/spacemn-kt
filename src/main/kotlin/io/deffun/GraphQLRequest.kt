package io.deffun

import io.micronaut.serde.annotation.Serdeable

@Serdeable
data class GraphQLRequest(
    val query: String? = null,
    val operationName: String? = null,
    val variables: Map<String, Any>? = null
)
