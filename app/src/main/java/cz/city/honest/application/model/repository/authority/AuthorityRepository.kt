package cz.city.honest.application.model.repository.authority

import android.content.ContentValues
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import cz.city.honest.application.model.dto.ExchangeRate
import cz.city.honest.application.model.repository.DatabaseOperationProvider
import cz.city.honest.application.model.repository.Repository
import cz.city.honest.application.model.repository.subject.exchange.ExchangePointRepository
import cz.city.honest.application.model.repository.subject.exchange.ExchangeRateRepository
import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.core.Maybe
import io.reactivex.rxjava3.core.Observable

class AuthorityRepository(
    databaseOperationProvider: DatabaseOperationProvider,
    private val exchangeRateRepository: ExchangeRateRepository
) : Repository<ExchangeRate>(databaseOperationProvider) {

    override fun insert(entity: ExchangeRate): Observable<Long> = Observable.just(
        databaseOperationProvider.writableDatabase.insertWithOnConflict(
            TABLE_NAME,
            "",
            getContentValues(entity),
            SQLiteDatabase.CONFLICT_REPLACE
        )
    ).flatMap { exchangeRateRepository.insert(entity) }

    override fun update(entity: ExchangeRate): Observable<Int> = Observable.just(
        databaseOperationProvider.writableDatabase.update(
            TABLE_NAME,
            getContentValues(entity),
            "where exchange_rates_id = ?",
            arrayOf(entity.id)
        )
    )

    override fun get(id: List<String>): Flowable<ExchangeRate> =
        Flowable.just(findAuthorityExchangeRates())
            .flatMap { toEntities(it) { exchangeRateRepository.get(getAsIdsList(it)) } }

    fun get(): Flowable<ExchangeRate> =
        Flowable.just(findAuthorityExchangeRates())
            .flatMap { toEntities(it) { exchangeRateRepository.get(getAsIdsList(it)) } }

    fun getOne(): Maybe<ExchangeRate> =
        Maybe.just(findAuthorityExchangeRates())
            .filter { cursorContainsData(it) }
            .flatMap { exchangeRateRepository.get(it.getString(0))  }

    override fun delete(entity: ExchangeRate): Observable<Int> = Observable.just(
        databaseOperationProvider.writableDatabase.delete(
            TABLE_NAME,
            "where id = ?",
            arrayOf(entity.id)
        )
    )

    fun delete(): Observable<Int> = get()
        .toObservable()
        .flatMap { exchangeRateRepository.delete(it) }
        .map {
            databaseOperationProvider.writableDatabase.delete(
                TABLE_NAME,
                null,
                null
            )
        }

    private fun getAsIdsList(cursor: Cursor) = listOf(cursor.getString(0))

    private fun getContentValues(entity: ExchangeRate) =
        ContentValues().apply {
            put("exchange_rates_id", entity.id)
        }

    private fun findAuthorityExchangeRates() =
        databaseOperationProvider.readableDatabase.rawQuery(
            "Select exchange_rates_id from authority",
            null
        )

    companion object {
        val TABLE_NAME = "authority"
    }

}