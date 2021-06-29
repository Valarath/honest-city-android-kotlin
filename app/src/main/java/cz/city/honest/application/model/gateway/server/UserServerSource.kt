package cz.city.honest.application.model.gateway.server

import cz.city.honest.application.model.dto.Suggestion
import cz.city.honest.application.model.gateway.UserGateway
import io.reactivex.rxjava3.core.Observable
import retrofit2.http.GET
import retrofit2.http.Header

interface UserServerSource : UserGateway {

    @GET(UserEndpointsUrl.USER_SUGGESTIONS)
    fun getUserSuggestions(request: GetUserSuggestionsRequest, @Header("Authorization") accessToken:String): Observable<GetUserSuggestionsResponse>

}

data class GetUserSuggestionsRequest(
    val userId: String
)

data class GetUserSuggestionsResponse(
    val userSuggestions: Map<Class<out Suggestion?>, List<Suggestion?>>

)

object UserEndpointsUrl {
    private const val USER_PREFIX = EndpointsUrl.PRIVATE + "/user"
    const val USER_SUGGESTIONS = "$USER_PREFIX/user-suggestions"
}