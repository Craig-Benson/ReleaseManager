package com.release_manager.util

import com.release_manager.model.inbound.InboundMessage
import com.release_manager.model.internal.InternalService
import lombok.extern.slf4j.Slf4j
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import java.time.LocalDateTime

@Component
@Slf4j
class ServiceMapper {

    val logger: Logger = LoggerFactory.getLogger(ServiceMapper::class.java)

    fun mapServices(
        systemVersionNumber: Int,
        currentlyDeployedServices: List<InternalService>,
        inboundMessage: InboundMessage
    ): HashMap<String, InternalService> {

        logger.info("Preparing current services and new service for deployment: $systemVersionNumber")
        val servicesMap = currentlyDeployedServices.associate {
            it.name to addService(systemVersionNumber, it.name, it.version, it.deployedAt)
        }.toMutableMap()

        servicesMap[inboundMessage.name] = addService(
            systemVersionNumber,
            inboundMessage.name,
            inboundMessage.version,
            inboundMessage.deployedAt
        )
        return HashMap(servicesMap);

    }

   private fun addService(
        systemVersionNumber: Int, name: String, version: Int, deployedAt: LocalDateTime
    ): InternalService {
        return InternalService(systemVersionNumber, name, version, deployedAt)
    }
}