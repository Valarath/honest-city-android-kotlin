package cz.city.honest.service.position

import cz.city.honest.dto.Position
import cz.city.honest.service.gateway.internal.InternalPositionGateway

class PositionService(private val internalPositionGateway: InternalPositionGateway) {

    fun saveCurrentUserPosition(position: Position) =
        internalPositionGateway
            .delete()
            .map { internalPositionGateway.insert(position) }

    fun getLastKnownUserPosition() = internalPositionGateway.get()

}