package cz.city.honest.repository.settings

import android.content.ContentValues
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import cz.city.honest.dto.CurrencySettings
import cz.city.honest.repository.DatabaseOperationProvider
import cz.city.honest.repository.Repository
import cz.city.honest.repository.toBoolean
import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.core.Observable

class CurrencySettingsRepository(databaseOperationProvider: DatabaseOperationProvider) :
    Repository<CurrencySettings>(databaseOperationProvider) {

    override fun insert(entity: CurrencySettings): Observable<Long> = Observable.just(
        databaseOperationProvider.writableDatabase.insertWithOnConflict(
            TABLE_NAME,
            "",
            getContentValues(entity),
            SQLiteDatabase.CONFLICT_REPLACE
        )
    )

    fun update(entity: CurrencySettings): Observable<Int> = Observable.just(
        databaseOperationProvider.writableDatabase.update(
            TABLE_NAME,
            getContentValues(entity),
            "id = ?",
            arrayOf(entity.id)
        )
    )

    fun get(): Flowable<CurrencySettings> =
        Flowable.just(findCurrencySettings())
            .flatMap { toEntities(it) { toCurrencySettings(it) } }

    fun delete(): Observable<Int> = Observable.just(
        databaseOperationProvider.writableDatabase.delete(
            TABLE_NAME,
            null,
            null
        )
    )

    private fun getContentValues(entity: CurrencySettings) =
        ContentValues().apply {
            put("id", entity.id)
            put("currency", entity.currency)
            put("main_country_currency", entity.mainCountryCurrency)
        }

    private fun toCurrencySettings(cursor: Cursor) = Flowable.just(
        CurrencySettings(
            id = cursor.getString(0),
            currency = cursor.getString(1),
            mainCountryCurrency = cursor.getInt(2).toBoolean()
        )
    )

    private fun findCurrencySettings() =
        databaseOperationProvider.readableDatabase.rawQuery(
            "Select id, currency, main_country_currency from currency_settings",
            null
        )

    companion object {
        val TABLE_NAME = "currency_settings"
    }


}