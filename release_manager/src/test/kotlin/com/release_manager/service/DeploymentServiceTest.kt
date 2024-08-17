package com.release_manager.service

import com.release_manager.controllers.DeploymentController
import com.release_manager.model.inbound.InboundMessage
import com.release_manager.repository.DeployedServicesRepository
import com.release_manager.util.ServiceMapper
import org.junit.jupiter.api.Assertions.*
import org.mockito.ArgumentMatchers.*
import org.mockito.Mockito.mock
import org.mockito.Mockito.`when`
import org.mockito.kotlin.mock
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.HttpStatus
import org.springframework.security.test.context.support.WithMockUser
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.setup.MockMvcBuilders

import java.time.LocalDateTime
import kotlin.test.BeforeTest
import kotlin.test.Test

@SpringBootTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
@WithMockUser
class DeploymentServiceTest(@Autowired serviceMapper: ServiceMapper) {

    @Autowired
    val deployedServicesRepository: DeployedServicesRepository = mock()
    @Autowired
    val deploymentService = DeploymentService(deployedServicesRepository, serviceMapper)

    @Autowired
    val deploymentController: DeploymentController = DeploymentController(deploymentService)

    lateinit var mockMvc: MockMvc

    @BeforeTest
    fun before() {
        mockMvc = MockMvcBuilders.standaloneSetup(deploymentController).build()
    }

    private val repository: DeployedServicesRepository = mock(DeployedServicesRepository::class.java)
    private val service = DeploymentService(repository, serviceMapper)

    @Test
    fun `test deployService returns conflict on duplicate message`() {
        val inboundMessage = InboundMessage("ServiceA", 1, LocalDateTime.now())
        `when`(repository.existsBySystemVersionNumberAndNameAndVersion(anyInt(), anyString(), anyInt()))
            .thenReturn(true)

        val response = service.deployService(inboundMessage)

        assertEquals(HttpStatus.CONFLICT, response.statusCode)
    }
}
