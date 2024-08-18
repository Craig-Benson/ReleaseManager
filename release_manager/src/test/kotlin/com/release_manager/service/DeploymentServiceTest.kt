package com.release_manager.service

import com.release_manager.model.inbound.InboundMessage
import com.release_manager.repository.DeployedServicesRepository
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Assertions.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.HttpStatus
import org.springframework.security.test.context.support.WithMockUser

import kotlin.test.BeforeTest
import kotlin.test.Test

@SpringBootTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
@WithMockUser
class DeploymentServiceTest(@Autowired val deployedServicesRepository: DeployedServicesRepository, @Autowired val deploymentService: DeploymentService) {

    @BeforeTest
    fun before() {
        deployedServicesRepository.deleteAll()
        deployedServicesRepository.flush()
    }

    @Test
    fun `Should respond with new system version number and persist when no previous service is deployed`() {
        val inboundMessage = InboundMessage("ServiceA", 1)

        val response = deploymentService.deployService(inboundMessage)

        val systemVersionNumber = 1
        response.statusCode shouldBe HttpStatus.CREATED
        response.body shouldBe systemVersionNumber
        deployedServicesRepository.count() shouldBe 1
    }

    @Test
    fun `Should respond with current system version number and not persist when service is already deployed`() {
        val inboundMessage = InboundMessage("ServiceA", 1)

        val firstResponse = deploymentService.deployService(inboundMessage)

        val systemVersionNumber = 1
        firstResponse.statusCode shouldBe HttpStatus.CREATED
        firstResponse.body shouldBe systemVersionNumber
        deployedServicesRepository.count() shouldBe 1

        val secondResponse = deploymentService.deployService(inboundMessage)


        secondResponse.statusCode shouldBe HttpStatus.CONFLICT
        secondResponse.body shouldBe systemVersionNumber
        deployedServicesRepository.count() shouldBe 1
    }

    @Test
    fun `Should respond with new system version number and update currently deployed services when a new service is deployed`() {

        val firstMessage = InboundMessage("ServiceA", 1)
        val secondMessage = InboundMessage("ServiceB", 2)

        val firstResponse = deploymentService.deployService(firstMessage)

        val initialSystemVersionNumber = 1
        assertEquals(HttpStatus.CREATED,  firstResponse.statusCode)
        assertEquals(initialSystemVersionNumber, firstResponse.body)

        val secondResponse = deploymentService.deployService(secondMessage)

        val updatedSystemVersionNumber = 2
        assertEquals(HttpStatus.CREATED,  secondResponse.statusCode)
        assertEquals(updatedSystemVersionNumber, secondResponse.body)

    }

}
