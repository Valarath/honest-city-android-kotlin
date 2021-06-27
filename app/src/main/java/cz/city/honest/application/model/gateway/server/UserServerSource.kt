package cz.city.honest.application.model.gateway.server

import cz.city.honest.application.model.dto.Suggestion
import cz.city.honest.application.model.gateway.UserGateway
import io.reactivex.rxjava3.core.Observable
import retrofit2.http.GET
import retrofit2.http.Header

interface UserServerSource : UserGateway {

    @GET("/user-suggestions")
    fun getUserSuggestions(request: GetUserSuggestionsRequest, @Header("Authorization") accessToken:String): Observable<GetUserSuggestionsResponse>

}

data class GetUserSuggestionsRequest(
    val userId: String
)

data class GetUserSuggestionsResponse(
    val userSuggestions: Map<Class<out Suggestion?>, List<Suggestion?>>

)