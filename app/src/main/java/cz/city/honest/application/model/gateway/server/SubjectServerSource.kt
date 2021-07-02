package cz.city.honest.application.model.gateway.server

import cz.city.honest.application.model.dto.WatchedSubject
import cz.city.honest.application.model.gateway.SubjectGateway
import io.reactivex.rxjava3.core.Observable
import retrofit2.http.GET
import retrofit2.http.QueryMap


interface SubjectServerSource : SubjectGateway {

    @GET(SubjectEndpointsUrl.SUBJECT_IN_AREA)
    fun getSubjectsInArea(@QueryMap request: Map<String, String>): Observable<GetSubjectsResponse>
}

data class GetSubjectsResponse(
    val subjects: MutableMap<String, List<WatchedSubject>>
)

object SubjectEndpointsUrl {
    private const val SUBJECT_PREFIX = EndpointsUrl.PUBLIC + "/subject"
    const val SUBJECT_IN_AREA = "$SUBJECT_PREFIX/subject-in-area"
}
