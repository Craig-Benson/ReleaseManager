package com.release_manager.service

import com.release_manager.model.internal.InternalService
import com.release_manager.repository.DeployedServicesRepository
import io.kotest.matchers.shouldBe
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.HttpStatus
import org.springframework.security.test.context.support.WithMockUser
import java.time.LocalDateTime
import kotlin.test.BeforeTest
import kotlin.test.Test

@SpringBootTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
@WithMockUser
class RetrievalServiceTest(
    @Autowired val deployedServicesRepository: DeployedServicesRepository,
    @Autowired val retrievalService: RetrievalService
) {

    @BeforeTest
    fun before() {
        deployedServicesRepository.deleteAll()
        deployedServicesRepository.flush()
    }


    @Test
    fun `Should respond with serviced when no matching service has been deployed`() {
        deployedServicesRepository.save(InternalService(1, "Service A", 1, LocalDateTime.now()))
        val response = retrievalService.retrieveServicesWithSystemVersionNumber(1)

        response.statusCode shouldBe HttpStatus.OK
    }

    @Test
    fun `Should respond with not found when no matching service has been deployed`() {
        val response = retrievalService.retrieveServicesWithSystemVersionNumber(1)

        response.statusCode shouldBe HttpStatus.NOT_FOUND
        deployedServicesRepository.count() shouldBe 0
    }

    @Test
    fun `Should respond with Internal server error when invalid system version number queried`() {
        val response = retrievalService.retrieveServicesWithSystemVersionNumber(-1)
        response.statusCode shouldBe HttpStatus.INTERNAL_SERVER_ERROR
    }
}