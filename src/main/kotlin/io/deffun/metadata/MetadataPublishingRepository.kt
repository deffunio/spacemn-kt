package io.deffun.metadata

import io.micronaut.context.event.ApplicationEventPublisher
import io.micronaut.runtime.context.scope.refresh.RefreshEvent
import org.neo4j.driver.Driver

class MetadataPublishingRepository(
    private val driver: Driver,
    private val eventPublisher: ApplicationEventPublisher<RefreshEvent>
) : MetadataRepository {
    private var metadataLoader: MetadataLoader = MetadataLoaderImpl(driver)

    override fun loadMetadata(apiId: String): Metadata {
        return metadataLoader.loadMetadata(apiId)
    }

    override fun saveGqlSchema(apiId: String, gqlSchema: String): Metadata = driver.session().use { session ->
        val result = session.run(
            "MERGE (api:API {id:\$id, schema:\$schema}) return api",
            mapOf(Pair("id", apiId), Pair("schema", gqlSchema))
        )
        val record = result.single()
        eventPublisher.publishEvent(RefreshEvent())
        return Metadata(gqlSchema, listOf())
    }

    override fun addOAuthConfig(apiId: String, oAuthConfig: OAuthConfig): Metadata {
        driver.session().use { session ->
            session.run(
                "MERGE (api:API {id:\$id})-[:OAUTH_USING {provider:\$providerName}]" +
                        "->(prov:Provider {clientId:\$clientId, clientSecret:\$clientSecret, issuer:\$issuer})",
                mapOf(
                    Pair("id", apiId), Pair("providerName", oAuthConfig.name),
                    Pair("clientId", oAuthConfig.clientId),
                    Pair("clientSecret", oAuthConfig.clientSecret),
                    Pair("issuer", oAuthConfig.issuer)
                )
            )
        }
        eventPublisher.publishEvent(RefreshEvent())
        return Metadata("", listOf())
    }
}
