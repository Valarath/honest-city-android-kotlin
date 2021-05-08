package cz.city.honest.application.model.repository.settings

import android.content.ContentValues
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import cz.city.honest.application.model.dto.CurrencySettings
import cz.city.honest.application.model.repository.DatabaseOperationProvider
import cz.city.honest.application.model.repository.Repository
import cz.city.honest.application.model.repository.subject.exchange.ExchangePointRepository
import cz.city.honest.application.model.repository.toBoolean
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

    override fun update(entity: CurrencySettings): Observable<Int> = Observable.just(
        databaseOperationProvider.writableDatabase.update(
            TABLE_NAME,
            getContentValues(entity),
            "where id = ?",
            arrayOf(entity.id)
        )
    )

    override fun get(ids: List<String>): Flowable<CurrencySettings> =
        Flowable.just(findLanguageSettings(ids))
            .flatMap { toEntities(it) { toLanguageSettings(it) } }

    fun get(): Flowable<CurrencySettings> =
        Flowable.just(findLanguageSettings())
            .flatMap { toEntities(it) { toLanguageSettings(it) } }

    override fun delete(entity: CurrencySettings): Observable<Int> = Observable.just(
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

    private fun getContentValues(entity: CurrencySettings) =
        ContentValues().apply {
            put("id", entity.id)
            put("language", entity.currency)
            put("main_country_currency", entity.mainCountryCurrency)
        }

    private fun toLanguageSettings(cursor: Cursor) = Flowable.just(
        CurrencySettings(
            id = cursor.getString(0),
            currency = cursor.getString(1),
            mainCountryCurrency = cursor.getInt(2).toBoolean()
        )
    )

    private fun findLanguageSettings() =
        databaseOperationProvider.readableDatabase.rawQuery(
            "Select id, language, main_country_currency from language_settings",
            null
        )

    private fun findLanguageSettings(ids: List<String>) =
        databaseOperationProvider.readableDatabase.rawQuery(
            "Select id, language, main_country_currency from language_settings",
            arrayOf(mapToQueryParamVariable(ids))
        )

    companion object {
        val TABLE_NAME = "currency_settings"
    }


}