package com.release_manager.service

import com.release_manager.model.inbound.InboundMessage
import com.release_manager.model.internal.InternalService
import com.release_manager.repository.DeployedServicesRepository
import com.release_manager.util.ServiceMapper
import lombok.extern.slf4j.Slf4j
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.dao.DataAccessException
import org.springframework.dao.DataIntegrityViolationException
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Service


@Service
@Slf4j
class DeploymentService(
    private val deployedServicesRepository: DeployedServicesRepository,
    private val serviceMapper: ServiceMapper,
) {
    val logger: Logger = LoggerFactory.getLogger(DeploymentService::class.java)
    var systemVersionNumber: Int = 0

    fun deployService(inboundMessage: InboundMessage): ResponseEntity<Int> {

        try {
            logger.info("Received new message")
            systemVersionNumber = deployedServicesRepository.findLatestSystemVersion() ?: 0

            isDuplicateMessage(inboundMessage, systemVersionNumber)

            var currentlyDeployedServices: List<InternalService> = mutableListOf()

            if (systemVersionNumber != 0) {
                logger.info("Retrieving currently deployed services")
                currentlyDeployedServices =
                    deployedServicesRepository.findAllBySystemVersionNumber(systemVersionNumber)
            }

            val newSystemVersionNumber = systemVersionNumber + 1;
            val servicesToDeploy: HashMap<String, InternalService> =
                serviceMapper.mapServices(newSystemVersionNumber, currentlyDeployedServices, inboundMessage)

            servicesToDeploy.forEach { entry -> persistService(entry.value) }

            return ResponseEntity.status(HttpStatus.CREATED).body(newSystemVersionNumber)

        } catch (ex: DataIntegrityViolationException) {
            logger.warn("Data integrity violation while deploying service. ${ex.message}")
            return ResponseEntity.status(HttpStatus.CONFLICT).body(systemVersionNumber)
        } catch (ex: DataAccessException) {
            logger.error("Data access violation while deploying service. ${ex.message}")
            return ResponseEntity.internalServerError().build()
        } catch (ex: Exception) {
            logger.error("Unexpected error while deploying service: ${ex.message}")
            return ResponseEntity.internalServerError().build()
        }
    }

    private fun isDuplicateMessage(inboundMessage: InboundMessage, currentSystemVersionNumber: Int) {
        if (deployedServicesRepository.existsBySystemVersionNumberAndNameAndVersion(
                currentSystemVersionNumber,
                inboundMessage.name,
                inboundMessage.version
            )
        ) {
            throw DataIntegrityViolationException("Duplicate entry, ${inboundMessage.name} is already present.")
        }
    }

    private fun persistService(internalService: InternalService) {
        logger.info("Persisting new service")
        deployedServicesRepository.save(internalService)
    }

}

