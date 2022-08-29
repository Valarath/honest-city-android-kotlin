package cz.city.honest.external

import cz.city.honest.dto.ExchangeRate
import cz.city.honest.service.gateway.external.AuthorityGateway
import io.reactivex.rxjava3.core.Observable
import retrofit2.http.GET

class AuthorityServerSourceService(private val authorityServerSource: AuthorityServerSource) : AuthorityGateway{

    override fun getRate(): Observable<ExchangeRate> =
        authorityServerSource
            .getRate()
            .map { it.exchangeRate }

}

interface AuthorityServerSource {

    @GET(AuthorityEndpointsUrl.GET_RATE)
    fun getRate(): Observable<GetCentralAuthorityRateResponse>

}

data class GetCentralAuthorityRateResponse(
    val exchangeRate: ExchangeRate
)

object AuthorityEndpointsUrl {

    private const val AUTHORITY_PREFIX: String = EndpointsUrl.PUBLIC + "/authority"
    const val GET_RATE = "$AUTHORITY_PREFIX/rate"

}