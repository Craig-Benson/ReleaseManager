package com.release_manager.controllers

import com.release_manager.repository.DeployedServicesRepository
import io.kotest.matchers.shouldBe
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.security.test.context.support.WithMockUser
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.post
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import kotlin.test.BeforeTest
import kotlin.test.Test

@SpringBootTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
@WithMockUser
class DeploymentControllerTest(@Autowired val deployedServicesRepository: DeployedServicesRepository,
    @Autowired val deploymentController: DeploymentController
) {

    lateinit var mockMvc: MockMvc

    @BeforeTest
    fun before() {
        mockMvc = MockMvcBuilders.standaloneSetup(deploymentController).build()
        deployedServicesRepository.deleteAll()
        deployedServicesRepository.flush()
    }

    @Test
    fun `Should respond with new system version number and persist when no previous service is deployed`() {
        val message = "{\"name\": \"Service A\",\"version\": 1}"
        mockMvc.post("/deploy") {
            contentType = MediaType.APPLICATION_JSON
            content = message
        }.andExpect { status { isCreated() } }

        deployedServicesRepository.count() shouldBe 1

    }

    @Test
    fun `Should respond with current system version number and not persist when duplicate service is deployed`() {
        val message = "{\"name\": \"Service A\",\"version\": 1}"

        mockMvc.post("/deploy") {
            contentType = MediaType.APPLICATION_JSON
            content = message
        }.andExpect { status { isCreated() } }

        mockMvc.post("/deploy") {
            contentType = MediaType.APPLICATION_JSON
            content = message
        }.andExpect { status { isConflict() } }

        deployedServicesRepository.count() shouldBe 1

    }

    @Test
    fun `Should respond with new system version number and update currently deployed services when a new service is deployed`() {
        val firstMessage = "{\"name\": \"Service A\",\"version\": 1}"
        val secondMessage = "{\"name\": \"Service B\",\"version\": 2}"

        val firstResult = mockMvc.post("/deploy") {
            contentType = MediaType.APPLICATION_JSON
            content = firstMessage
        }.andExpect { status { isCreated() } }
            .andReturn()

        val expectedInitialSystemVersion = "1"
        firstResult.response.contentAsString shouldBe expectedInitialSystemVersion
        deployedServicesRepository.count() shouldBe 1

        val secondResult = mockMvc.post("/deploy") {
            contentType = MediaType.APPLICATION_JSON
            content = secondMessage
        }.andExpect { status { isCreated() } }
            .andReturn()

        val expectedUpdatedSystemVersion = "2"
        secondResult.response.contentAsString shouldBe expectedUpdatedSystemVersion
        deployedServicesRepository.count() shouldBe 3

    }

    @Test
    fun `Should respond with bad request when no name present on message`() {
        val message = "{\"version\": 1}"
        mockMvc.post("/deploy") {
            contentType = MediaType.APPLICATION_JSON
            content = message
        }.andExpect { status { isBadRequest() } }

        deployedServicesRepository.count() shouldBe 0
    }

    @Test
    fun `Should respond with bad request when name is empty on message`() {
        val message = "{\"name\": \"\",\"version\": 0}"
        mockMvc.post("/deploy") {
            contentType = MediaType.APPLICATION_JSON
            content = message
        }.andExpect { status { isBadRequest() } }

        deployedServicesRepository.count() shouldBe 0
    }

    @Test
    fun `Should respond with bad request when no version present on message`() {
        val message = "{\"name\": \"Service A\"}"
        mockMvc.post("/deploy") {
            contentType = MediaType.APPLICATION_JSON
            content = message
        }.andExpect { status { isBadRequest() } }

        deployedServicesRepository.count() shouldBe 0
    }

    @Test
    fun `Should respond with bad request when both name and version are not present on message`() {
        val message = "{}"
        mockMvc.post("/deploy") {
            contentType = MediaType.APPLICATION_JSON
            content = message
        }.andExpect { status { isBadRequest() } }

        deployedServicesRepository.count() shouldBe 0
    }

    @Test
    fun `Should respond with bad request when version is less than 1 on message`() {
        val message = "{\"name\": \"Service A\",\"version\": 0}"
        mockMvc.post("/deploy") {
            contentType = MediaType.APPLICATION_JSON
            content = message
        }.andExpect { status { isBadRequest() } }

        deployedServicesRepository.count() shouldBe 0

    }

    @Test
    fun `Should respond with bad request when version is empty on message`() {
        val message = "{\"name\": \"Service A\",\"version\": }"
        mockMvc.post("/deploy") {
            contentType = MediaType.APPLICATION_JSON
            content = message
        }.andExpect { status { isBadRequest() } }

        deployedServicesRepository.count() shouldBe 0

    }

}
