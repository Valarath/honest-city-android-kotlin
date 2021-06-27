package cz.city.honest.application.model.gateway.server

import cz.city.honest.application.model.dto.Position
import cz.city.honest.application.model.dto.WatchedSubject
import cz.city.honest.application.model.gateway.SubjectGateway
import io.reactivex.rxjava3.core.Observable
import retrofit2.http.GET


interface SubjectServerSource : SubjectGateway {

    @GET("/subjects-in-area")
    fun getSubjectsInArea(request: GetSubjectsRequest): Observable<GetSubjectsResponse>
}

data class GetSubjectsRequest(
    val userPosition: Position
)

data class GetSubjectsResponse(
    val subjects: MutableMap<Class<out WatchedSubject>, List<WatchedSubject>>
)