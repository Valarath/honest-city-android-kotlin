package cz.city.honest.application.model.gateway.server

import cz.city.honest.application.model.gateway.SubjectGateway
import cz.city.honest.mobile.model.dto.Position
import cz.city.honest.mobile.model.dto.WatchedSubject
import reactor.core.publisher.Mono
import retrofit2.http.GET


interface SubjectServerSource : SubjectGateway {

    @GET("/subjects-in-area")
    fun getSubjectsInArea(request: GetSubjectsRequest): Mono<GetSubjectsResponse>
}

data class GetSubjectsRequest(
    val userPosition: Position
)

data class GetSubjectsResponse(
    val subjects: Map<Class<out WatchedSubject>, List<WatchedSubject>>
)