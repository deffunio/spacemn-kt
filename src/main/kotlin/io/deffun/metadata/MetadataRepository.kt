package io.deffun.metadata

interface MetadataRepository : MetadataLoader {
    fun saveGqlSchema(apiId: String, gqlSchema: String): Metadata

    fun addOAuthConfig(apiId: String, oAuthConfig: OAuthConfig): Metadata
}
