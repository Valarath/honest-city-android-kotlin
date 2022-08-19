package cz.city.honest.external

import cz.city.honest.dto.Suggestion
import io.reactivex.rxjava3.core.Observable
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.QueryMap

interface UserServerSource {

    @GET(UserEndpointsUrl.USER_SUGGESTIONS)
    fun getUserSuggestions(@QueryMap request: Map<String, String>, @Header("Authorization") accessToken:String): Observable<GetUserSuggestionsResponse>

}

data class GetUserSuggestionsResponse(
    val userSuggestions: Map<String, List<Suggestion?>>

)

object UserEndpointsUrl {
    private const val USER_PREFIX = EndpointsUrl.PRIVATE + "/user"
    const val USER_SUGGESTIONS = "$USER_PREFIX/user-suggestions"
}