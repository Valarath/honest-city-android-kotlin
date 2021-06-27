package cz.city.honest.application.model.gateway.server

import cz.city.honest.application.model.dto.ExchangeRate
import cz.city.honest.application.model.gateway.AuthorityGateway
import io.reactivex.rxjava3.core.Observable
import retrofit2.http.GET

interface AuthorityServerSource : AuthorityGateway {

    @GET("/rate")
    fun getRate(): Observable<GetCentralAuthorityRateResponse>

}


data class GetCentralAuthorityRateResponse(
    val exchangeRate: ExchangeRate
)