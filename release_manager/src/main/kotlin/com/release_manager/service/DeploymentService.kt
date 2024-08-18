package com.release_manager.service

import com.release_manager.model.inbound.InboundMessage
import com.release_manager.model.internal.InternalService
import com.release_manager.repository.DeployedServicesRepository
import com.release_manager.util.ServiceMapper
import lombok.extern.slf4j.Slf4j
import org.hibernate.query.sqm.tree.SqmNode.log
import org.springframework.dao.DataAccessException
import org.springframework.dao.DataIntegrityViolationException
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Service
import javax.imageio.plugins.tiff.ExifInteroperabilityTagSet


@Service
@Slf4j
class DeploymentService(
    private val deployedServicesRepository: DeployedServicesRepository,
    private val serviceMapper: ServiceMapper,
) {
    var systemVersionNumber: Int = 0

    fun deployService(inboundMessage: InboundMessage): ResponseEntity<Int> {

        try {
            log.info("Received new message")
            systemVersionNumber = deployedServicesRepository.findLatestSystemVersion() ?: 0

            isDuplicateMessage(inboundMessage, systemVersionNumber)

            var currentlyDeployedServices: List<InternalService> = mutableListOf()

            if (systemVersionNumber != 0) {
                log.info("Retrieving currently deployed services")
                currentlyDeployedServices =
                    deployedServicesRepository.findAllBySystemVersionNumber(systemVersionNumber)
            }

            val newSystemVersionNumber = systemVersionNumber + 1;
            val servicesToDeploy: HashMap<String, InternalService> =
                serviceMapper.mapServices(newSystemVersionNumber, currentlyDeployedServices, inboundMessage)

            servicesToDeploy.forEach { entry -> persistService(entry.value) }

            return ResponseEntity.status(HttpStatus.CREATED).body(newSystemVersionNumber)

        } catch (ex: DataIntegrityViolationException) {
            log.warn("Data integrity violation while deploying service. ${ex.message}")
            return ResponseEntity.status(HttpStatus.CONFLICT).body(systemVersionNumber)
        } catch (ex: DataAccessException) {
            log.warn("Data access violation while deploying service. ${ex.message}")
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(-1)
        } catch (ex: Exception) {
            log.error("Unexpected error while deploying service: ${ex.message}")
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(-1)
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
        log.info("Persisting new service")
        deployedServicesRepository.save(internalService)
    }

}

