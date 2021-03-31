package cz.city.honest.application.model.gateway.server

import cz.city.honest.application.model.gateway.AuthorityGateway
import cz.city.honest.mobile.model.dto.ExchangeRate
import io.reactivex.rxjava3.core.Observable
import reactor.core.publisher.Mono
import retrofit2.http.GET

interface AuthorityServerSource : AuthorityGateway {

    @GET("/rate")
    fun getRate(): Observable<GetCentralAuthorityRateResponse>

}


data class GetCentralAuthorityRateResponse(
    val exchangeRate: ExchangeRate
)