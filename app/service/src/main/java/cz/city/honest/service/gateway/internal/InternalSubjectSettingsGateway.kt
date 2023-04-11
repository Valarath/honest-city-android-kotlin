package cz.city.honest.service.gateway.internal

import cz.city.honest.dto.SubjectSettings
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Single

interface InternalSubjectSettingsGateway {

    fun insert(settings: SubjectSettings):Single<Unit>

    fun delete():Observable<Unit>

    fun get():Single<SubjectSettings>

}