package cz.city.honest.application.model.server

import cz.city.honest.application.model.dto.Suggestion
import cz.city.honest.application.model.gateway.UserGateway
import reactor.core.publisher.Mono
import retrofit2.http.GET
import javax.inject.Singleton

@Singleton
interface UserServerSource : UserGateway {

    @GET("/user-suggestions")
    fun getUserSuggestions(request: GetUserSuggestionsRequest): Mono<GetUserSuggestionsResponse>

}

data class GetUserSuggestionsRequest(
    val userId: Long
)

data class GetUserSuggestionsResponse(
    val userSuggestions: Map<Class<out Suggestion?>, List<Suggestion?>>

)