package com.release_manager.controllers

import com.release_manager.model.outbound.OutboundMessage
import com.release_manager.repository.DeployedServicesRepository
import com.release_manager.service.RetrievalService
import io.github.resilience4j.ratelimiter.annotation.RateLimiter
import jakarta.validation.Valid
import jakarta.validation.constraints.Digits
import jakarta.validation.constraints.Min
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.time.LocalDateTime
import java.util.*


@RestController
class ServicesController(val retrievalService: RetrievalService) {

    @GetMapping("/services")
    @RateLimiter(name="default")
    fun getServices(
        @Valid
        @RequestParam
        @Min(value = 1L, message = "System version number must be greater than 0")
        @Digits(integer = 10, fraction = 0, message = "System Version must a number")
         systemVersionNumber: Int
    ): ResponseEntity<List<OutboundMessage>> {
        return retrievalService.retrieveServicesWithSystemVersionNumber(systemVersionNumber)
    }
}
