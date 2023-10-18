package io.deffun.metadata

import io.micronaut.context.env.ActiveEnvironment
import io.micronaut.context.env.PropertySource
import io.micronaut.context.env.PropertySourceLoader
import io.micronaut.core.io.ResourceLoader
import org.neo4j.driver.AuthTokens
import org.neo4j.driver.GraphDatabase
import java.io.InputStream
import java.util.Optional

class MetadataPropertySourceLoader : PropertySourceLoader {
    private var metadataLoader: MetadataLoader

    init {
        val driver = GraphDatabase.driver(System.getenv(DATABASE_KEY), AuthTokens.basic("", ""))
        metadataLoader = MetadataLoaderImpl(driver)
    }

    override fun load(resourceName: String?, resourceLoader: ResourceLoader?): Optional<PropertySource> {
        return loadResource(resourceLoader, resourceName)
    }

    override fun read(name: String?, input: InputStream?): MutableMap<String, Any> {
        TODO("Not yet implemented")
    }

    override fun loadEnv(
        resourceName: String?,
        resourceLoader: ResourceLoader?,
        activeEnvironment: ActiveEnvironment?
    ): Optional<PropertySource> {
        return loadResource(resourceLoader, resourceName + "-" + activeEnvironment?.name)
    }

    private fun loadResource(resourceLoader: ResourceLoader?, resourceName: String?): Optional<PropertySource> {
        val apiId = System.getenv(API_ID_KEY)
        val metadata = metadataLoader.loadMetadata(apiId)
        val oauthProvidersMap = mutableMapOf<String, Map<String, Any>>()
        for (oauthConfig in metadata.oAuthConfigs) {
            oauthProvidersMap[oauthConfig.name] = mapOf(
                Pair("client-id", oauthConfig.clientId),
                Pair("client-secret", oauthConfig.clientSecret),
                Pair("openid", mapOf(Pair("issuer", oauthConfig.issuer)))
            )
        }
        return Optional.of(PropertySource.of(mapOf(Pair("micronaut.security.oauth2.clients", oauthProvidersMap))))
    }
}
