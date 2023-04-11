package cz.city.honest.service.gateway.external

import cz.city.honest.dto.SubjectSettings
import io.reactivex.rxjava3.core.Single

interface ExternalSubjectSettingsGateway {

    fun get(): Single<SubjectSettings>

}