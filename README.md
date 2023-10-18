# SpaceMN (Kotlin edition)

GraphQL server on top of the Micronaut framework.

```cypher
CREATE (api:Api {name: "My Api"})-[:OAUTH_WITH {provider: "google"}]->(prov:Provider {clientId: "xxx"})
```

```cypher
MERGE (api:Api {name: "My Api"})-[:OAUTH_WITH {provider: "google"}]->(prov:Provider {clientId: "xxx"})
```
