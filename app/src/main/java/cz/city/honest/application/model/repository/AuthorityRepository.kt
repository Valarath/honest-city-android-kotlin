package cz.city.honest.application.model.repository

import android.database.Cursor
import cz.city.honest.application.model.gateway.AuthorityGateway
import cz.city.honest.mobile.model.dto.ExchangeRate
import io.reactivex.rxjava3.core.Observable
import reactor.core.publisher.Mono

class AuthorityRepository (
    private val databaseOperationProvider: DatabaseOperationProvider,
    private val exchangeRateRepository: ExchangeRateRepository
) : AuthorityGateway {

    //fun deleteExchangeRate()= databaseOperationProvider.writableDatabase.delete()

    fun getExchangeRate(): Observable<ExchangeRate> =
        Observable.just(findAuthorityExchangeRates())
            .flatMap {
                it.moveToNext()
                exchangeRateRepository.getExchangeRates(it.getLong(0))
            }

    private fun findAuthorityExchangeRates(): Cursor {
        return databaseOperationProvider.readableDatabase.rawQuery(
            "Select exchange_rates_id from authority_has_exchange_rate",
            null
        )
    }


}