package com.release_manager.model.internal

import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.Id
import java.time.LocalDateTime

@Entity
class InternalService(
    var systemVersionNumber: Int?,
    var name: String,
    var version: Int,
    var deployedAt: LocalDateTime
) {

    @Id
    @GeneratedValue
    var id: Long? = null

}