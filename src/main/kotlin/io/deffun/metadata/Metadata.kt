package io.deffun.metadata

import io.micronaut.serde.annotation.Serdeable

@Serdeable
data class Metadata(
    val gqlSchema: String? = null,
    val oAuthConfigs: List<OAuthConfig> = emptyList()
)
