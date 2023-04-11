package cz.city.honest.external.settings

import cz.city.honest.dto.SubjectSettings
import cz.city.honest.external.subject.SubjectEndpointsUrl
import cz.city.honest.service.gateway.external.ExternalSubjectSettingsGateway
import io.reactivex.rxjava3.core.Single
import retrofit2.http.GET

class ExternalSubjectSettingsServerSourceService(private val subjectSettingsServerSource: SubjectSettingsServerSource): ExternalSubjectSettingsGateway {

    override fun get(): Single<SubjectSettings> =
        subjectSettingsServerSource
            .getCurrenciesSettings()
            .map { it.subjectSettings }
}

interface SubjectSettingsServerSource {

    @GET(ExchangeNameEndpointsUrl.GET_EXCHANGE_NAME_SETTINGS)
    fun getCurrenciesSettings(): Single<GetSubjectSettingsResponse>
}

data class GetSubjectSettingsResponse(val subjectSettings: SubjectSettings)

object ExchangeNameEndpointsUrl {

    private const val EXCHANGE_NAME_PREFIX: String = SubjectEndpointsUrl.SUBJECT_PREFIX + "/settings"
    const val GET_EXCHANGE_NAME_SETTINGS = "$EXCHANGE_NAME_PREFIX/get"

}