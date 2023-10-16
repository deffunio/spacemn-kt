package io.deffun.metadata

import io.micronaut.context.annotation.Value
import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Post
import io.micronaut.http.annotation.Put

@Controller("metadata")
class MetadataKontroller(
    private val metadataRepository: MetadataRepository,
    @Value("\${api.id}") private val apiId: String
) {
    @Post(value = "add_oauth", consumes = ["application/json"])
    fun addOAuth(oAuthConfig: OAuthConfig): Metadata {
        return metadataRepository.addOAuthConfig(apiId, oAuthConfig)
    }

    @Put(consumes = ["application/json"])
    fun update(input: MetadataInput): Metadata {
        return metadataRepository.saveGqlSchema(apiId, input.gqlSchema ?: "")
    }
}
