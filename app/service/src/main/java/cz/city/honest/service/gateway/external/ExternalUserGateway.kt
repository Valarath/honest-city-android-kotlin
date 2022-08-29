package cz.city.honest.service.gateway.external

import cz.city.honest.dto.Suggestion
import cz.city.honest.dto.User
import io.reactivex.rxjava3.core.Observable

interface ExternalUserGateway {

    fun getUserSuggestions(
        user: User,
        accessToken: String
    ): Observable<UserSuggestions>
}

data class UserSuggestions(
    val userSuggestions: Map<String, List<Suggestion?>>
)