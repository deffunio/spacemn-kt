package io.deffun.metadata

interface MetadataLoader {
    fun loadMetadata(apiId: String): Metadata
}
