package cz.city.honest.repository.authority

import android.content.ContentValues
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import com.fasterxml.jackson.databind.ObjectMapper
import cz.city.honest.dto.ExchangeRate
import cz.city.honest.repository.DatabaseOperationProvider
import cz.city.honest.repository.Repository
import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.core.Observable

class AuthorityRepository(
    databaseOperationProvider: DatabaseOperationProvider,
    private val objectMapper: ObjectMapper
) : Repository<ExchangeRate>(databaseOperationProvider) {

    override fun insert(entity: ExchangeRate): Observable<Long> = Observable.just(
        databaseOperationProvider.writableDatabase.insertWithOnConflict(
            TABLE_NAME,
            "",
            getContentValues(entity),
            SQLiteDatabase.CONFLICT_REPLACE
        )
    )

    fun get(): Flowable<ExchangeRate> =
        Flowable.just(findAuthorityExchangeRates())
            .flatMap { toEntities(it) { toWatchedSubject(it) } }

    fun delete(): Observable<Int> = get()
        .toObservable()
        .map {
            databaseOperationProvider.writableDatabase.delete(
                TABLE_NAME,
                null,
                null
            )
        }

    private fun getContentValues(entity: ExchangeRate) =
        ContentValues().apply {
            put("id", entity.id)
            put("data", objectMapper.writeValueAsString(entity))
        }

    private fun toWatchedSubject(cursor: Cursor): Flowable<ExchangeRate> = Flowable.just(
        objectMapper.readValue(cursor.getString(1), ExchangeRate::class.java)
    )

    private fun findAuthorityExchangeRates() =
        databaseOperationProvider.readableDatabase.rawQuery(
            "Select id, data from authority",
            null
        )

    companion object {
        val TABLE_NAME = "authority"
    }

}