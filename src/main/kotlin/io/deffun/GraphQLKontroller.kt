package io.deffun

import graphql.ExecutionInput
import graphql.GraphQL
import graphql.schema.GraphQLCodeRegistry
import graphql.schema.idl.RuntimeWiring
import graphql.schema.idl.SchemaGenerator
import graphql.schema.idl.SchemaParser
import io.deffun.metadata.MetadataRepository
import io.micronaut.context.annotation.Value
import io.micronaut.http.annotation.Body
import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Post
import io.micronaut.runtime.context.scope.Refreshable
import jakarta.annotation.PostConstruct
import kotlinx.coroutines.future.await
import org.neo4j.graphql.SchemaBuilder
import org.neo4j.graphql.SchemaConfig

@Refreshable
@Controller("graphql")
class GraphQLKontroller(
    @Value("\${api.id}") val apiId: String,
    val metadataRepository: MetadataRepository
) {
    private lateinit var graphQL: GraphQL

    // FIXME: Called on every request to /graphql
    @PostConstruct
    fun createGraphQL() {
        val metadata = metadataRepository.loadMetadata(apiId)

        val typeDefinitionRegistry = SchemaParser().parse(metadata.gqlSchema)

        val builder = RuntimeWiring.newRuntimeWiring()

        val codeRegistryBuilder = GraphQLCodeRegistry.newCodeRegistry()
        val config = SchemaConfig()
        val schemaBuilder = SchemaBuilder(typeDefinitionRegistry, config)
        schemaBuilder.augmentTypes()
        schemaBuilder.registerScalars(builder)
        schemaBuilder.registerTypeNameResolver(builder)

        val runtimeWiring = builder.codeRegistry(codeRegistryBuilder).build()
        val graphQLSchema = SchemaGenerator().makeExecutableSchema(typeDefinitionRegistry, runtimeWiring)
        graphQL = GraphQL.newGraphQL(graphQLSchema).build()
    }

    @Post
    suspend fun post(@Body body: GraphQLRequest): Any? {
        val executionInput = ExecutionInput.newExecutionInput()
            .query(body.query)
            .operationName(body.operationName)
            .variables(body.variables)
            .build()
        return graphQL.executeAsync(executionInput).await()
    }
}
