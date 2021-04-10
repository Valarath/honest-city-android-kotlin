package cz.city.honest.application.model.repository.subject.exchange

import android.content.ContentValues
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import cz.city.honest.application.model.dto.ClosedExchangePointSuggestion
import cz.city.honest.application.model.repository.DatabaseOperationProvider
import cz.city.honest.application.model.repository.Repository
import cz.city.honest.mobile.model.dto.*
import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.core.Observable
import java.time.LocalDate

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

    override fun delete(entity: ExchangeRate): Observable<Int> = Observable.concat(
        deleteExchangeRates(entity.id),
        deleteExchangeRate(entity.id)
    )

    private fun updateExchangeRates(entity: ExchangeRate)= Observable.just(
        databaseOperationProvider.writableDatabase.update(
            EXCHANGE_RATES_TABLE,
            getExchangeRatesContentValues(entity),
            "id = ?",
            arrayOf(entity.id.toString())
    ))

    private fun updateExchangeRate(exchangeRatesId: String, rate: Rate)= Observable.just(
        databaseOperationProvider.writableDatabase.update(
            EXCHANGE_RATE_TABLE,
            getExchangeRateContentValues(exchangeRatesId,rate),
            "exchange_rates_id = ?",
            arrayOf(exchangeRatesId.toString())
    ))



    private fun findExchangeRates(subjectIds: List<String>): Flowable<Cursor> =
        Flowable.just(
            databaseOperationProvider.readableDatabase.rawQuery(
                "Select id, buy, currency from exchange_rate where exchange_rates.id in( ${
                    mapToQueryParamSymbols(
                        subjectIds
                    )
                })",
                arrayOf(mapToQueryParamVariable(subjectIds))
            )
        )

    private fun toExchangeRate(cursor: Cursor) = Flowable.just(
        ExchangeRate(
            id = cursor.getString(0),
            watched = Watched(LocalDate.now(), LocalDate.now()),
            rates = getExchangeRates(cursor)
        ))


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
            put("exchange_rates_id", exchangeRatesId)
            put("buy", rate.rateValues.buy)
            put("sell", (rate.rateValues as ExchangePointRateValues).sell)
            put("currency", rate.currency)
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

    companion object{
        val EXCHANGE_RATES_TABLE="exchange_rates"
        val EXCHANGE_RATE_TABLE="exchange_rate"
    }

}