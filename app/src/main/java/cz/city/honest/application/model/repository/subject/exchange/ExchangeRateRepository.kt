package cz.city.honest.application.model.repository.subject.exchange

import android.content.ContentValues
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import cz.city.honest.application.model.dto.*
import cz.city.honest.application.model.repository.DatabaseOperationProvider
import cz.city.honest.application.model.repository.Repository
import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.core.Maybe
import io.reactivex.rxjava3.core.Observable
import java.time.LocalDate
import java.util.*

class ExchangeRateRepository (
    databaseOperationProvider: DatabaseOperationProvider
):Repository<ExchangeRate>(databaseOperationProvider) {


    override fun insert(entity: ExchangeRate): Observable<Long> =
        Observable.just(entity)
            .flatMap { addExchangeRates(entity) }
            .flatMap { addExchangeRate(entity) }

    override fun update(entity: ExchangeRate): Observable<Int> = Observable.just(entity)
    .flatMap { updateExchangeRate(entity) }
    .flatMap { updateExchangeRates(entity) }

    override fun get(id: List<String>): Flowable<ExchangeRate> = findExchangeRates(id)
    .flatMap { toEntities(it) { toExchangeRate(it) } }

    fun get(id: String): Maybe<ExchangeRate> =
        findExchangeRate(id)
            .filter { cursorContainsData(it) }
            .map { asExchangeRate(it) }


    override fun delete(entity: ExchangeRate): Observable<Int> = Observable.concat(
        deleteExchangeRates(entity.id),
        deleteExchangeRate(entity.id)
    )

    private fun updateExchangeRates(entity: ExchangeRate)= Observable.just(
        databaseOperationProvider.writableDatabase.update(
            EXCHANGE_RATES_TABLE,
            getExchangeRatesContentValues(entity),
            "id = ?",
            arrayOf(entity.id)
    ))

    private fun updateExchangeRate(exchangeRatesId: String, rate: Rate)= Observable.just(
        databaseOperationProvider.writableDatabase.update(
            EXCHANGE_RATE_TABLE,
            getExchangeRateContentValues(exchangeRatesId,rate),
            "exchange_rates_id = ?",
            arrayOf(exchangeRatesId)
    ))



    private fun findExchangeRates(subjectIds: List<String>): Flowable<Cursor> =
        Flowable.just(getFindExchangeRateCursor(subjectIds))

    private fun findExchangeRate(id:String) = Maybe.just(getFindExchangeRateCursor(listOf(id)))

    private fun getFindExchangeRateCursor(subjectIds: List<String>) =
        databaseOperationProvider.readableDatabase.rawQuery(
            "Select id, buy, currency from exchange_rate where exchange_rates_id in( ${
                mapToQueryParamSymbols(
                    subjectIds
                )
            })",
            arrayOf(mapToQueryParamVariable(subjectIds))
        )

    private fun toExchangeRate(cursor: Cursor) = Flowable.just(
        asExchangeRate(cursor)
    )

    private fun asExchangeRate(cursor: Cursor) = ExchangeRate(
        id = cursor.getString(0),
        watched = Watched(LocalDate.now(), LocalDate.now()),
        rates = getExchangeRates(cursor)
    )


    private fun addExchangeRates(exchangeRate: ExchangeRate) = Observable.just(
        databaseOperationProvider.writableDatabase.insertWithOnConflict(
            EXCHANGE_RATES_TABLE,
            null,
            getExchangeRatesContentValues(exchangeRate),
            SQLiteDatabase.CONFLICT_REPLACE
        )
    )

    private fun addExchangeRate(exchangeRate: ExchangeRate) =
        Observable.just(exchangeRate)
            .map { exchangeRate.rates.forEach { insertExchangeRate(exchangeRate.id, it) } }
            .map {0L  }

    private fun updateExchangeRate(exchangeRate: ExchangeRate) =
        Observable.just(exchangeRate)
            .map { exchangeRate.rates.forEach { updateExchangeRate(exchangeRate.id, it) } }
            .map {0  }


    private fun insertExchangeRate(
        exchangeRatesId: String,
        rate: Rate
    ) {
        databaseOperationProvider.writableDatabase.insertWithOnConflict(
            EXCHANGE_RATE_TABLE,
            null,
            getExchangeRateContentValues(exchangeRatesId, rate),
            SQLiteDatabase.CONFLICT_REPLACE
        )
    }

    private fun getExchangeRateContentValues(exchangeRatesId: String, rate: Rate) =
        ContentValues().apply {
            put("id",exchangeRatesId)
            put("exchange_rates_id", exchangeRatesId)
            put("buy", rate.rateValues.buy)
            put("currency", rate.currency)
        }.also {
            setSellProperty(rate, it, rate.rateValues)
        }

    private fun setSellProperty(
        rate: Rate,
        it: ContentValues,
        rateValues: ExchangeRateValues
    ) {
        if (rate.rateValues is ExchangePointRateValues)
            it.put("sell", (rateValues as ExchangePointRateValues).sell)
    }


    private fun getExchangeRatesContentValues(entity: ExchangeRate) =
        ContentValues().apply {
            put("id", entity.id)
        }

    private fun deleteExchangeRate(exchangeRatesId: String): Observable<Int> =
        Observable.just(
            databaseOperationProvider.writableDatabase.delete(
                EXCHANGE_RATE_TABLE,
                "exchange_rates_id=?",
                arrayOf(exchangeRatesId)
            )
        )


    private fun deleteExchangeRates(exchangeRatesId: String): Observable<Int> =
        Observable.just(
            databaseOperationProvider.writableDatabase.delete(
                EXCHANGE_RATES_TABLE,
                "id=?",
                arrayOf(exchangeRatesId)
            )
        )

    private fun getExchangeRates(cursor: Cursor): MutableSet<Rate> {
        val rates = mutableSetOf<Rate>()
        rates.add(toRate(cursor))
        while (cursor.moveToNext())
            rates.add(toRate(cursor))
        return rates;
    }

    private fun toRate(cursor: Cursor): Rate =
        Rate(
            currency = cursor.getString(2).toUpperCase(Locale.getDefault()),
            rateValues = ExchangeRateValues(
                buy = cursor.getDouble(1)
            )
        )

    companion object{
        val EXCHANGE_RATES_TABLE="exchange_rates"
        val EXCHANGE_RATE_TABLE="exchange_rate"
    }

}