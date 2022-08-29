package cz.city.honest.external

import cz.city.honest.dto.Position
import cz.city.honest.dto.Suggestion
import cz.city.honest.dto.WatchedSubject
import cz.city.honest.service.gateway.external.ExternalSubjectGateway
import cz.city.honest.service.gateway.external.Subjects
import io.reactivex.rxjava3.core.Observable
import retrofit2.http.GET
import retrofit2.http.QueryMap

class SubjectServerSourceService(private val subjectServerSource: SubjectServerSource)
    :ExternalSubjectGateway{

    override fun getSubjectsInArea(position: Position): Observable<Subjects> =
        subjectServerSource.getSubjectsInArea(toRequest(position))
            .map { Subjects(it.subjects,it.newSubjectSuggestions) }

    private fun toRequest(position: Position) = mapOf(
        "userPosition.latitude" to position.latitude.toString(),
        "userPosition.longitude" to position.longitude.toString()
    )
}

interface SubjectServerSource {

    @GET(SubjectEndpointsUrl.SUBJECT_IN_AREA)
    fun getSubjectsInArea(@QueryMap request: Map<String, String>): Observable<GetSubjectsResponse>
}

data class GetSubjectsResponse(
    val subjects: MutableMap<String, List<WatchedSubject>>,
    val newSubjectSuggestions: MutableMap<String, List<Suggestion>>
)

object SubjectEndpointsUrl {
    private const val SUBJECT_PREFIX = EndpointsUrl.PUBLIC + "/subject"
    const val SUBJECT_IN_AREA = "$SUBJECT_PREFIX/subject-in-area"
}
