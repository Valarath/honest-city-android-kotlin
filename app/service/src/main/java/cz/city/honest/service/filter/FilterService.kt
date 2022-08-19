package cz.city.honest.service.filter

import cz.city.honest.dto.Filter
import io.reactivex.rxjava3.core.Single

class FilterService(private val filterPersistenceHandler: FilterPersistenceHandler) {

    fun getFilter() = filterPersistenceHandler.getFilter()

    fun setFilter(filter: Filter) = filterPersistenceHandler.persistFilter(filter)

}

//TODO prejmenuj premisti atd...
interface FilterPersistenceHandler{

    fun getFilter(): Single<Filter>

    fun persistFilter(filter: Filter): Single<Filter>

}