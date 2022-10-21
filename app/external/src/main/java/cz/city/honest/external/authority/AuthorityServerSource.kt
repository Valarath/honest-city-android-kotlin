package cz.city.honest.external.authority

import cz.city.honest.dto.ExchangeRate
import cz.city.honest.external.EndpointsUrl
import cz.city.honest.service.gateway.external.ExternalAuthorityGateway
import io.reactivex.rxjava3.core.Observable
import retrofit2.http.GET

class AuthorityServerSourceService(private val authorityServerSource: AuthorityServerSource) : ExternalAuthorityGateway{

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