package com.release_manager.controllers

import com.release_manager.repository.DeployedServicesRepository
import io.kotest.matchers.shouldBe
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.security.test.context.support.WithMockUser
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.get
import org.springframework.test.web.servlet.post
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import kotlin.test.BeforeTest
import kotlin.test.Test

@SpringBootTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
@WithMockUser
class ServicesControllerTest(
    @Autowired val deployedServicesRepository: DeployedServicesRepository,
    @Autowired val servicesController: ServicesController,
    @Autowired val deploymentController: DeploymentController
) {
    lateinit var mockMvcServicesController: MockMvc
    lateinit var mockMvcDeploymentController: MockMvc

    @BeforeTest
    fun before() {
        mockMvcServicesController = MockMvcBuilders.standaloneSetup(servicesController).build()
        mockMvcDeploymentController = MockMvcBuilders.standaloneSetup(deploymentController).build()
        deployedServicesRepository.deleteAll()
        deployedServicesRepository.flush()
    }

    @Test
    fun `Should return single services associated with system version number`() {
        val firstMessage = "{\"name\": \"Service A\",\"version\": 1}"
        postMessage(firstMessage)
        val systemVersionNumber = "1"

        val result = mockMvcServicesController.get("/services") {
            param("systemVersionNumber", systemVersionNumber)
        }
            .andExpect { status { isOk() } }
            .andReturn()

        val expectedResult = "[{\"name\":\"Service A\",\"version\":1}]"
        result.response.contentAsString shouldBe expectedResult
        deployedServicesRepository.count() shouldBe 1
    }

    @Test
    fun `Should return multiple services associated with system version number`() {
        val firstMessage = "{\"name\": \"Service A\",\"version\": 1}"
        val secondMessage = "{\"name\": \"Service B\",\"version\": 1}"
        postMessage(firstMessage)
        postMessage(secondMessage)

        val systemVersionNumber = "2"
        val result = mockMvcServicesController.get("/services") {
            param("systemVersionNumber", systemVersionNumber)
        }
            .andExpect { status { isOk() } }
            .andReturn()

        val expectedResult = "[{\"name\":\"Service A\",\"version\":1},{\"name\":\"Service B\",\"version\":1}]"
        result.response.contentAsString shouldBe expectedResult
        deployedServicesRepository.count() shouldBe 3
    }


    @Test
    fun `Should return not found when no service associated with requested system version number`() {
        val firstMessage = "{\"name\": \"Service A\",\"version\": 1}"
        postMessage(firstMessage)
        val systemVersionNumber = "2"

    mockMvcServicesController.get("/services") {
            param("systemVersionNumber", systemVersionNumber)
        }
            .andExpect { status { isNotFound() } }

    }


    @Test
    fun `Should return bad request when requested system version number is negative`() {
        val firstMessage = "{\"name\": \"Service A\",\"version\": 1}"
        postMessage(firstMessage)
        val systemVersionNumber = "-1"

        mockMvcServicesController.get("/services") {
            param("systemVersionNumber", systemVersionNumber)
        }
            .andExpect { status { isBadRequest() } }

    }


    @Test
    fun `Should return bad request when requested system version number is zero`() {
        val firstMessage = "{\"name\": \"Service A\",\"version\": 1}"
        postMessage(firstMessage)
        val systemVersionNumber = "0"

        mockMvcServicesController.get("/services") {
            param("systemVersionNumber", systemVersionNumber)
        }
            .andExpect { status { isBadRequest() } }

    }


    private fun postMessage(message: String) {
        mockMvcDeploymentController.post("/deploy") {
            contentType = MediaType.APPLICATION_JSON
            content = message
        }.andExpect { status { isCreated() } }
    }
}