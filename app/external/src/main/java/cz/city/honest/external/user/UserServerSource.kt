package cz.city.honest.external.user

import cz.city.honest.dto.Suggestion
import cz.city.honest.dto.User
import cz.city.honest.external.EndpointsUrl
import cz.city.honest.service.gateway.external.ExternalUserGateway
import cz.city.honest.service.gateway.external.UserSuggestions
import io.reactivex.rxjava3.core.Observable
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.QueryMap

class UserServerSourceService(private val userServerSource: UserServerSource) :
    ExternalUserGateway {

    override fun getUserSuggestions(
        user: User,
        accessToken: String,
    ): Observable<UserSuggestions> =
        userServerSource.getUserSuggestions(getGetUserSuggestionsRequest(user),accessToken)
            .map { UserSuggestions(it.userSuggestions) }

    private fun getGetUserSuggestionsRequest(user: User) = mapOf("userId" to user.id)

}

interface UserServerSource {

    @GET(UserEndpointsUrl.USER_SUGGESTIONS)
    fun getUserSuggestions(
        @QueryMap request: Map<String, String>,
        @Header("Authorization") accessToken: String
    ): Observable<GetUserSuggestionsResponse>

}

data class GetUserSuggestionsResponse(
    val userSuggestions: Map<String, List<Suggestion?>>

)

object UserEndpointsUrl {
    private const val USER_PREFIX = EndpointsUrl.PRIVATE + "/user"
    const val USER_SUGGESTIONS = "$USER_PREFIX/user-suggestions"
}