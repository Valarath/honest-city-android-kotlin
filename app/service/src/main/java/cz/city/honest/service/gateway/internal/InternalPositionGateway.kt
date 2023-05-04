package cz.city.honest.service.gateway.internal

import cz.city.honest.dto.Position
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Single

interface InternalPositionGateway {

    fun get():Single<Position>

    fun delete():Observable<Unit>

    fun insert(entity: Position): Observable<Unit>

}