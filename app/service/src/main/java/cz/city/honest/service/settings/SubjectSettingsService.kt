package cz.city.honest.service.settings

import cz.city.honest.dto.SubjectSettings
import cz.city.honest.service.gateway.external.ExternalSubjectSettingsGateway
import cz.city.honest.service.gateway.internal.InternalSubjectSettingsGateway
import cz.city.honest.service.update.PublicUpdatable
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Single

class SubjectSettingsService(
    private val internalSubjectSettingsGateway: InternalSubjectSettingsGateway,
    private val externalSubjectSettingsGateway: ExternalSubjectSettingsGateway
) : PublicUpdatable {

    override fun update(): Observable<Unit> =
        internalSubjectSettingsGateway.delete().doOnTerminate { getNewData() }
            .onErrorComplete()
            .map { }

    private fun getNewData() = externalSubjectSettingsGateway
        .get()
        .flatMap { insert(it) }
        .subscribe()


    private fun insert(settings: SubjectSettings) =
        internalSubjectSettingsGateway.insert(settings)

    fun get(): Single<SubjectSettings> = internalSubjectSettingsGateway.get()
}