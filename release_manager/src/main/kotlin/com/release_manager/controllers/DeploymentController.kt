package com.release_manager.controllers

import com.release_manager.model.inbound.InboundMessage
import com.release_manager.service.DeploymentService
import io.swagger.v3.oas.annotations.Operation
import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@RestController
class DeploymentController(
    private val deploymentService: DeploymentService
) {

    @PostMapping("/deploy")
    @Operation(method = "Used for notifying new deployments")
    fun deploy(@Valid @RequestBody inboundMessage: InboundMessage): ResponseEntity<Int> {
        return deploymentService.deployService(inboundMessage)
    }


}