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
class DeploymentControllerTest(
    @Autowired val deployedServicesRepository: DeployedServicesRepository,
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
    fun `When a post with a valid service not previously deployed is received, should persist and respond with new system version number `() {
        val message = "{\"name\": \"Service A\",\"version\": 1}"
        mockMvc.post("/deploy") {
            contentType = MediaType.APPLICATION_JSON
            content = message
        }.andExpect { status { isCreated() } }

        deployedServicesRepository.count() shouldBe 1

    }

    @Test
    fun `When multiple valid post for different services are received, should update currently deployed services, persist and respond with new system version number`() {
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
    fun `When a post with a duplicate service is received, should not persist and respond with current system version number`() {
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
    fun `When a post with no name is received, should respond with bad request`() {
        val message = "{\"version\": 1}"
        mockMvc.post("/deploy") {
            contentType = MediaType.APPLICATION_JSON
            content = message
        }.andExpect { status { isBadRequest() } }

        deployedServicesRepository.count() shouldBe 0
    }

    @Test
    fun `When a post with blank name is received, should respond with bad request`() {
        val message = "{\"name\": \"\",\"version\": 0}"
        mockMvc.post("/deploy") {
            contentType = MediaType.APPLICATION_JSON
            content = message
        }.andExpect { status { isBadRequest() } }

        deployedServicesRepository.count() shouldBe 0
    }

    @Test
    fun `When a post with no version is received, should respond with bad request`() {
        val message = "{\"name\": \"Service A\"}"
        mockMvc.post("/deploy") {
            contentType = MediaType.APPLICATION_JSON
            content = message
        }.andExpect { status { isBadRequest() } }

        deployedServicesRepository.count() shouldBe 0
    }

    @Test
    fun `When a post with no name and no version is receive, should respond with bad request`() {
        val message = "{}"
        mockMvc.post("/deploy") {
            contentType = MediaType.APPLICATION_JSON
            content = message
        }.andExpect { status { isBadRequest() } }

        deployedServicesRepository.count() shouldBe 0
    }

    @Test
    fun `When a post with a version less than 1 is received, should respond with bad request`() {
        val message = "{\"name\": \"Service A\",\"version\": 0}"
        mockMvc.post("/deploy") {
            contentType = MediaType.APPLICATION_JSON
            content = message
        }.andExpect { status { isBadRequest() } }

        deployedServicesRepository.count() shouldBe 0

    }

    @Test
    fun `When a post with blank version is received, should respond with bad request`() {
        val message = "{\"name\": \"Service A\",\"version\": }"
        mockMvc.post("/deploy") {
            contentType = MediaType.APPLICATION_JSON
            content = message
        }.andExpect { status { isBadRequest() } }

        deployedServicesRepository.count() shouldBe 0

    }

}
