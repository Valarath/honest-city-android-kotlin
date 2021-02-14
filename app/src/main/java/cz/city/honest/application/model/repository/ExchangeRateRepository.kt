package cz.city.honest.application.model.repository

import android.content.ContentValues
import android.database.Cursor
import cz.city.honest.application.model.gateway.AuthorityGateway
import cz.city.honest.mobile.model.dto.*
import io.reactivex.rxjava3.core.Observable
import reactor.core.publisher.Mono
import java.time.LocalDate

class ExchangeRateRepository (
    private val databaseOperationProvider: DatabaseOperationProvider
) : AuthorityGateway {

    fun getExchangeRates(exchangeRatesId: Long): Observable<ExchangeRate> =
        Observable.just(
            databaseOperationProvider.readableDatabase.rawQuery(
                "Select id, buy, currency from exchange_rate where exchange_rates.id = ?",
                arrayOf(exchangeRatesId.toString())
            )
        )
            .map { toExchangeRate(it) }

    private fun toExchangeRate(cursor: Cursor): ExchangeRate =
        ExchangeRate(
            id = cursor.getLong(0),
            watched = Watched(LocalDate.now(), LocalDate.now()),
            rates = getExchangeRates(cursor)
        ).also {
            cursor.close()
        }

    fun getExchangePointRates(exchangePointId: Long): Mono<ExchangeRate> =
        Mono.just(
            databaseOperationProvider.readableDatabase.rawQuery(
                "Select exchange_rate.id, buy, currency from exchange_rate join exchange_rates on exchange_rate.exchange_rates_id=exchange_point_has_exchange_rate.exchange_rates_id where exchange_point.id = ?",
                arrayOf(exchangePointId.toString())
            )
        )
            .map { toExchangeRate(it) }

    fun addExchangeRates(exchangePoint: ExchangePoint): Mono<ExchangeRate> =
        Mono.just(exchangePoint)
            .flatMap { addExchangeRates(exchangePoint.exchangePointRate.id) }
            .flatMap { addExchangeRate(exchangePoint.exchangePointRate) }


    private fun addExchangeRates(exchangeRatesId: Long) = Mono.just(
        databaseOperationProvider.writableDatabase.insert(
            "exchange_rates",
            null,
            ContentValues().apply { put("id", exchangeRatesId) }
        )
    )

    private fun addExchangeRate(exchangeRate: ExchangeRate) =
        Mono.just(exchangeRate)
            .map { exchangeRate.rates.forEach { insertExchangeRate(exchangeRate.id, it) } }
            .map { exchangeRate }

    private fun insertExchangeRate(
        exchangeRatesId: Long,
        it: Rate
    ) {
        databaseOperationProvider.writableDatabase.insert(
            "exchange_rate",
            null,
            ContentValues().apply {
                put("exchange_rates_id", exchangeRatesId)
                put("buy", it.rateValues.buy)
                put("sell", (it.rateValues as ExchangePointRateValues).sell)
                put("currency", it.currency)
            }
        )
    }

    fun removeExchangeRate(exchangeRatesId: Long): Mono<Unit> =
        Mono.zip(
            deleteExchangeRates(exchangeRatesId),
            deleteExchangeRate(exchangeRatesId)
        ).map { Unit }

    private fun deleteExchangeRate(exchangeRatesId: Long): Mono<Int> =
        Mono.just(
            databaseOperationProvider.writableDatabase.delete(
                "exchange_rate",
                "exchange_rates_id=?",
                arrayOf(exchangeRatesId.toString())
            )
        )


    private fun deleteExchangeRates(exchangeRatesId: Long): Mono<Int> =
        Mono.just(
            databaseOperationProvider.writableDatabase.delete(
                "exchange_rates",
                "id=?",
                arrayOf(exchangeRatesId.toString())
            )
        )


    private fun getExchangeRates(cursor: Cursor): HashSet<Rate> {
        val rates = HashSet<Rate>()
        while (cursor.moveToNext())
            rates.add(toRate(cursor))
        return rates;
    }

    private fun toRate(cursor: Cursor): Rate =
        Rate(
            currency = cursor.getString(1),
            rateValues = ExchangeRateValues(
                buy = cursor.getDouble(2)
            )
        )

}