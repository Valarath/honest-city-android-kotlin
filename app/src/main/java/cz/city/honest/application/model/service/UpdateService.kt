package cz.city.honest.application.model.service

import io.reactivex.rxjava3.core.Observable

class UpdateService(
    val updatableServices:List<Updatable>
){

    fun update(): Observable<Unit> = Observable
        .fromIterable(updatableServices)
        .flatMap { it.update() }

}

interface Updatable{

    fun update():Observable<Unit>

}