package cz.city.honest.application.model.repository.settings

import android.content.ContentValues
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import cz.city.honest.application.model.dto.CurrencySetting
import cz.city.honest.application.model.repository.DatabaseOperationProvider
import cz.city.honest.application.model.repository.Repository
import cz.city.honest.application.model.repository.subject.exchange.ExchangePointRepository
import cz.city.honest.application.model.repository.toBoolean
import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.core.Observable

class CurrencySettingsRepository(databaseOperationProvider: DatabaseOperationProvider) :
    Repository<CurrencySetting>(databaseOperationProvider) {

    override fun insert(entity: CurrencySetting): Observable<Long> = Observable.just(
        databaseOperationProvider.writableDatabase.insertWithOnConflict(
            TABLE_NAME,
            "",
            getContentValues(entity),
            SQLiteDatabase.CONFLICT_REPLACE
        )
    )

    override fun update(entity: CurrencySetting): Observable<Int> = Observable.just(
        databaseOperationProvider.writableDatabase.update(
            TABLE_NAME,
            getContentValues(entity),
            "where id = ?",
            arrayOf(entity.id)
        )
    )

    override fun get(ids: List<String>): Flowable<CurrencySetting> =
        Flowable.just(findCurrencySettings(ids))
            .flatMap { toEntities(it) { toCurrencySettings(it) } }

    fun get(): Flowable<CurrencySetting> =
        Flowable.just(findCurrencySettings())
            .flatMap { toEntities(it) { toCurrencySettings(it) } }

    override fun delete(entity: CurrencySetting): Observable<Int> = Observable.just(
        databaseOperationProvider.writableDatabase.delete(
            ExchangePointRepository.TABLE_NAME,
            "where id = ?",
            arrayOf(entity.id)
        )
    )

    fun delete(): Observable<Int> = Observable.just(
        databaseOperationProvider.writableDatabase.delete(
            ExchangePointRepository.TABLE_NAME,
            null,
            null
        )
    )

    private fun getAsIdsList(cursor: Cursor) = listOf(cursor.getString(0))

    private fun getContentValues(entity: CurrencySetting) =
        ContentValues().apply {
            put("id", entity.id)
            put("currency", entity.currency)
            put("main_country_currency", entity.mainCountryCurrency)
        }

    private fun toCurrencySettings(cursor: Cursor) = Flowable.just(
        CurrencySetting(
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

    private fun findCurrencySettings(ids: List<String>) =
        databaseOperationProvider.readableDatabase.rawQuery(
            "Select id, currency, main_country_currency from currency_settings",
            arrayOf(mapToQueryParamVariable(ids))
        )

    companion object {
        val TABLE_NAME = "currency_settings"
    }


}