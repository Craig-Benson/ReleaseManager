package com.release_manager.util

import com.release_manager.model.inbound.InboundMessage
import com.release_manager.model.internal.InternalService
import lombok.extern.slf4j.Slf4j
import org.hibernate.query.sqm.tree.SqmNode.log
import org.springframework.stereotype.Component
import java.time.LocalDateTime

@Component
@Slf4j
class ServiceMapper {

    //todo test
    fun mapServices(
        systemVersionNumber: Int,
        currentlyDeployedServices: List<InternalService>,
        inboundMessage: InboundMessage
    ): HashMap<String, InternalService> {

        log.info("Preparing current services and new service for deployment: $systemVersionNumber")
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