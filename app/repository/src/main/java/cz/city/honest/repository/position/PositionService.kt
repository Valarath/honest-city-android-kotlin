package cz.city.honest.repository.position

import cz.city.honest.dto.Position
import cz.city.honest.service.gateway.internal.InternalPositionGateway

class PositionService(private val positionRepository: PositionRepository) :
    InternalPositionGateway {

    override fun get() = positionRepository.get()

    override fun delete() = positionRepository
        .delete()
        .map { }

    override fun insert(entity: Position) = positionRepository
        .insert(entity)
        .map { }

}