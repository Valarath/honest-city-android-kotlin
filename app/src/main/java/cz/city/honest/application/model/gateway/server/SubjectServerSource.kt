package cz.city.honest.application.model.gateway.server

import cz.city.honest.application.model.dto.Position
import cz.city.honest.application.model.dto.WatchedSubject
import cz.city.honest.application.model.gateway.SubjectGateway
import io.reactivex.rxjava3.core.Observable
import retrofit2.http.GET


interface SubjectServerSource : SubjectGateway {

    @GET(SubjectEndpointsUrl.SUBJECT_IN_AREA)
    fun getSubjectsInArea(request: GetSubjectsRequest): Observable<GetSubjectsResponse>
}

data class GetSubjectsRequest(
    val userPosition: Position
)

data class GetSubjectsResponse(
    val subjects: MutableMap<Class<out WatchedSubject>, List<WatchedSubject>>
)

object SubjectEndpointsUrl {
    private const val SUBJECT_PREFIX = EndpointsUrl.PUBLIC + "/subject"
    const val SUBJECT_IN_AREA = "$SUBJECT_PREFIX/subject-in-area"
}
