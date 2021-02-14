package cz.city.honest.application.model.gateway.server

import cz.city.honest.application.model.gateway.AuthorityGateway
import cz.city.honest.mobile.model.dto.ExchangeRate
import reactor.core.publisher.Mono
import retrofit2.http.GET

interface AuthorityServerSource : AuthorityGateway {

    @GET("/rate")
    fun getRate(): Mono<GetCentralAuthorityRateResponse>

}


data class GetCentralAuthorityRateResponse(
    val exchangeRate: ExchangeRate
)