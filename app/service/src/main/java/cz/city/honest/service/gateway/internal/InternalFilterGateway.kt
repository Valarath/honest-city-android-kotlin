package cz.city.honest.service.gateway.internal

import cz.city.honest.dto.Filter
import io.reactivex.rxjava3.core.Single

interface InternalFilterGateway{

    fun getFilter(): Single<Filter>

    fun persistFilter(filter: Filter): Single<Filter>

}