package io.deffun.metadata

import io.micronaut.serde.annotation.Serdeable

@Serdeable
data class OAuthConfig(
    var name: String,
    var clientId: String,
    var clientSecret: String,
    var issuer: String
)
