package com.release_manager.model.inbound

import io.swagger.v3.oas.annotations.Hidden
import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.Digits
import jakarta.validation.constraints.Min
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Size
import java.time.LocalDateTime

data class InboundMessage(
    @field:NotNull @field:Size(min = 1, message = "Name required")
    @field:Schema(example = "Service A")
    val name: String,

    @field:NotNull(message = "Version required")
    @field:Min(value = 1L, message = "Version must greater than 0")
    @field:Digits(integer = 10, fraction = 0, message = "Version must a number")
    val version: Int,
    @Hidden
    val deployedAt: LocalDateTime = LocalDateTime.now())