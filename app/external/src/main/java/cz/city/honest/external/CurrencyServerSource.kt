package cz.city.honest.external

import cz.city.honest.dto.CurrencySettings
import io.reactivex.rxjava3.core.Observable
import retrofit2.http.GET

interface CurrencyServerSource {

    @GET(CurrencyEndpointsUrl.GET_CURRENCY_SETTINGS)
    fun getCurrenciesSettings():Observable<GetCurrenciesSettingsResponse>
}

data class GetCurrenciesSettingsResponse(val currencySettings: List<CurrencySettings>)

object CurrencyEndpointsUrl{

    private const val CURRENCY_PREFIX: String = EndpointsUrl.PUBLIC + "/currency/settings"
    const val GET_CURRENCY_SETTINGS = "$CURRENCY_PREFIX/get"

}