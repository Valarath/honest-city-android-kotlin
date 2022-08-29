package cz.city.honest.service.filter

import cz.city.honest.dto.Filter
import cz.city.honest.service.gateway.internal.InternalFilterGateway

class FilterService(private val internalFilterGateway: InternalFilterGateway) {

    fun getFilter() = internalFilterGateway.getFilter()

    fun setFilter(filter: Filter) = internalFilterGateway.persistFilter(filter)

}
