package cz.city.honest.application.model.gateway.server

import cz.city.honest.application.model.dto.User
import io.reactivex.rxjava3.core.Observable
import retrofit2.http.POST

interface FacebookLoginServerSource{

    @POST("/login/facebook")
    fun loginFacebookUser(request:PostLoginFacebookUserRequest): Observable<PostLoginFacebookUserResponse>

}

data class PostLoginFacebookUserRequest(val accessToken:String)

data class PostLoginFacebookUserResponse(val user: User)
