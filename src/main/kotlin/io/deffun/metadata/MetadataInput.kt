package io.deffun.metadata

import io.micronaut.serde.annotation.Serdeable

@Serdeable
data class MetadataInput(
    val gqlSchema: String? = null
)
