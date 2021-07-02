package cz.city.honest.application.model.gateway.server

import cz.city.honest.application.model.dto.Position
import cz.city.honest.application.model.dto.WatchedSubject
import cz.city.honest.application.model.gateway.SubjectGateway
import io.reactivex.rxjava3.core.Observable
import retrofit2.http.GET
import retrofit2.http.Query


interface SubjectServerSource : SubjectGateway {

    @GET(SubjectEndpointsUrl.SUBJECT_IN_AREA)
    fun getSubjectsInArea(@Query("longitude")longitude:Double,@Query("latitude") latitude: Double): Observable<GetSubjectsResponse>
}

data class GetSubjectsRequest(
    val userPosition: Position
)

data class GetSubjectsResponse(
    val subjects: MutableMap<String, List<WatchedSubject>>
)

object SubjectEndpointsUrl {
    private const val SUBJECT_PREFIX = EndpointsUrl.PUBLIC + "/subject"
    const val SUBJECT_IN_AREA = "$SUBJECT_PREFIX/subject-in-area"
}
