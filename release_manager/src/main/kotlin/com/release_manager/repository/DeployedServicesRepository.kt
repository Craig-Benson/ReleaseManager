package com.release_manager.repository

import com.release_manager.model.internal.InternalService
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository

@Repository
interface DeployedServicesRepository : JpaRepository<InternalService, Long> {

    @Query("SELECT s.systemVersionNumber FROM InternalService s ORDER BY s.systemVersionNumber DESC LIMIT 1")
    fun findLatestSystemVersion(): Int?

    fun existsBySystemVersionNumberAndNameAndVersion(
        systemVersionNumber: Int, name: String,
        version: Int
    ): Boolean

    fun findAllBySystemVersionNumber(systemVersionNumber: Int): List<InternalService>
}