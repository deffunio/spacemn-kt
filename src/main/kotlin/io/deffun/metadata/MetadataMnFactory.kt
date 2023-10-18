package io.deffun.metadata

import io.micronaut.context.annotation.Bean
import io.micronaut.context.annotation.Factory
import io.micronaut.context.event.ApplicationEventPublisher
import io.micronaut.runtime.context.scope.refresh.RefreshEvent
import org.neo4j.driver.Driver

@Factory
class MetadataMnFactory {
    @Bean
    fun metadataRepository(driver: Driver, eventPublisher: ApplicationEventPublisher<RefreshEvent>): MetadataRepository {
        return MetadataPublishingRepository(driver, eventPublisher)
    }
}
