package com.release_manager.util

import com.release_manager.model.inbound.InboundMessage
import com.release_manager.model.internal.InternalService
import io.kotest.matchers.shouldBe
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import java.time.LocalDateTime
import kotlin.test.Test

@SpringBootTest
class ServiceMapperTest(@Autowired val serviceMapper: ServiceMapper) {

    @Test
    fun `Should map currently deployed services and new service to the same system version number`() {
        val currentlyDeployedService = InternalService(1, "Service A", 1, LocalDateTime.now())
        val newService = InboundMessage("Service B", 1)
        val systemVersionNumber = 1

        val currentlyDeployedServices = listOf(currentlyDeployedService)
        val actual = serviceMapper.mapServices(systemVersionNumber, currentlyDeployedServices, newService);

        actual.values.first().name shouldBe newService.name
        actual.values.first().version shouldBe newService.version
        actual.values.first().systemVersionNumber shouldBe systemVersionNumber

        actual.values.last().name shouldBe currentlyDeployedService.name
        actual.values.last().version shouldBe currentlyDeployedService.version
        actual.values.last().systemVersionNumber shouldBe systemVersionNumber

        actual.count() shouldBe 2


    }

    @Test
    fun `Should replace previously deployed service with new service version and map new system version`() {
        val newService = InboundMessage("Service B", 2)
        val systemVersionNumber = 2
        val currentlyDeployedServices = listOf(
            InternalService(1, "Service A", 1, LocalDateTime.now()),
            InternalService(1, "Service B", 1, LocalDateTime.now())
        )

        val actual = serviceMapper.mapServices(systemVersionNumber, currentlyDeployedServices, newService);

        actual.values.first().name shouldBe currentlyDeployedServices.last().name
        actual.values.first().version shouldBe newService.version
        actual.values.first().systemVersionNumber shouldBe systemVersionNumber

        actual.values.last().name shouldBe currentlyDeployedServices.first().name
        actual.values.last().version shouldBe currentlyDeployedServices.first().version
        actual.values.last().systemVersionNumber shouldBe systemVersionNumber
        actual.count() shouldBe 2


    }
}