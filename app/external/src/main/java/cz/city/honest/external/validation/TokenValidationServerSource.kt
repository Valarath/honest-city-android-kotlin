package cz.city.honest.external.validation

import cz.city.honest.service.gateway.external.ExternalTokenValidationGateway
import io.reactivex.rxjava3.core.Observable
import retrofit2.http.Body
import retrofit2.http.POST

class TokenValidationServerSourceService(private val validationServerSource: TokenValidationServerSource):
    ExternalTokenValidationGateway {

    override fun isValid(token: String): Observable<Boolean> =
        validationServerSource.validate(PostValidateTokenRequest(token = token))
            .map { it.valid }

}

interface TokenValidationServerSource {

    @POST(TokenValidationUrl.TOKEN_VALIDATION_URL)
    fun validate(@Body request: PostValidateTokenRequest): Observable<PostValidateTokenResponse>

}

data class PostValidateTokenRequest(val token: String)

data class PostValidateTokenResponse(val valid: Boolean)

class TokenValidationUrl {
    companion object {
        const val TOKEN_VALIDATION_URL = "${ValidationUrl.VALIDATION_PREFIX}/token"
    }
}