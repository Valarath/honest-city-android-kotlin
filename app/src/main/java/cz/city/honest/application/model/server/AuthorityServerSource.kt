package cz.city.honest.application.model.server

import cz.city.honest.application.model.gateway.AuthorityGateway
import cz.city.honest.mobile.model.dto.ExchangeRate
import reactor.core.publisher.Mono
import retrofit2.http.GET
import javax.inject.Singleton

@Singleton
interface AuthorityServerSource : AuthorityGateway {

    @GET("/rate")
    fun getRate(): Mono<GetCentralAuthorityRateResponse>

}


data class GetCentralAuthorityRateResponse(
    private val exchangeRate: ExchangeRate
)