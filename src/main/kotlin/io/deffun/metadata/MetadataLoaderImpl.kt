package io.deffun.metadata

import io.deffun.stringOrEmpty
import org.neo4j.driver.Driver

class MetadataLoaderImpl(private val driver: Driver) : MetadataLoader {
    override fun loadMetadata(apiId: String): Metadata = driver.session().use { session ->
        val result = session.run(
            "MATCH (api:API {id:\$id}) OPTIONAL MATCH (api)-[prov]->(provider) RETURN api.gqlSchema as gqlSchema, " +
                    "prov as providerName, provider.clientId as clientId, provider.clientSecret as clientSecret, provider.issuer as issuer",
            mapOf(Pair("id", apiId))
        )
        val gqlSchema = result.peek().get("gqlSchema").stringOrEmpty()
        val list = result.list()
        val map = list.filter { record -> !record.get("providerName").isNull }
            .map { record ->
                val providerName = record.get("providerName").get("provider").stringOrEmpty()
                val clientId = record.get("clientId").stringOrEmpty()
                val clientSecret = record.get("clientSecret").stringOrEmpty()
                val issuer = record.get("issuer").stringOrEmpty()
                OAuthConfig(providerName, clientId, clientSecret, issuer)
            }
        return Metadata(gqlSchema, map)
    }
}
