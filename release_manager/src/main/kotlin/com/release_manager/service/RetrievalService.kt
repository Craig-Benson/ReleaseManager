package com.release_manager.service

import com.release_manager.model.outbound.OutboundMessage
import com.release_manager.repository.DeployedServicesRepository
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.dao.DataAccessException
import org.springframework.dao.DataIntegrityViolationException
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Component
import java.time.LocalDateTime
import java.util.*

@Component
class RetrievalService(val deployedServicesRepository: DeployedServicesRepository) {
    fun retrieveServicesWithSystemVersionNumber(systemVersionNumber: Int): ResponseEntity<List<OutboundMessage>> {
        val logger: Logger = LoggerFactory.getLogger(RetrievalService::class.java)
        try {
            if (systemVersionNumber < 1) {
                throw DataIntegrityViolationException("Invalid version number present")
            }
            val services = deployedServicesRepository.findAllBySystemVersionNumber(systemVersionNumber)

            if (services.isEmpty()) {
                logger.info("No records found")
                return ResponseEntity.notFound().build()
            }

            val sortedServices: TreeMap<LocalDateTime, OutboundMessage> = TreeMap()

            services.forEach { service ->
                sortedServices[service.deployedAt] = OutboundMessage(service.name, service.version)
            }

            val outboundMessages = ArrayList(sortedServices.values);

            return ResponseEntity.ok().body(outboundMessages)
        } catch (ex: DataAccessException) {
            logger.info("Failed while retrieving service. ${ex.message}")
            return ResponseEntity.internalServerError().build()
        } catch (ex: Exception) {
            logger.error("Unexpected error while retrieving service: ${ex.message}")
            return ResponseEntity.internalServerError().build()
        }
    }

}