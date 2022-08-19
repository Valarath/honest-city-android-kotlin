package cz.city.honest.repository.subject.exchange

import android.content.ContentValues
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import cz.city.honest.repository.DatabaseOperationProvider
import cz.city.honest.repository.subject.SubjectRepository
import cz.city.honest.repository.suggestion.SuggestionRepository
import cz.city.honest.dto.*
import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.core.Observable
import java.time.LocalDate

class ExchangePointRepository(
    databaseOperationProvider: DatabaseOperationProvider,
    suggestionRepositories: Map<String, SuggestionRepository<out Suggestion>>,
    exchangeRateRepository: ExchangeRateRepository
) : SubjectRepository<ExchangePoint>(
    databaseOperationProvider,
    suggestionRepositories,
    exchangeRateRepository
) {
    override fun insert(entity: ExchangePoint): Observable<Long> = super.insert(entity)
        .map {
            databaseOperationProvider.writableDatabase.insertWithOnConflict(
                TABLE_NAME,
                "",
                getContentValues(entity),
                SQLiteDatabase.CONFLICT_REPLACE
            )
        }
        .filter { entity.exchangePointRate != null }
        .flatMap { exchangeRateRepository.insert(entity.exchangePointRate!!) }

    override fun update(entity: ExchangePoint): Observable<Int> =
        super.update(entity)
            .map {
                databaseOperationProvider.writableDatabase.update(
                    TABLE_NAME,
                    getContentValues(entity),
                    "id = ?",
                    arrayOf(entity.id)
                )
            }
            .filter { entity.exchangePointRate != null }
            .flatMap { exchangeRateRepository.update(entity.exchangePointRate!!) }

    override fun get(id: List<String>): Flowable<ExchangePoint> =
        findExchangePoint(id)
            .flatMap { toEntities(it) { toExchangePoint(it) } }

    override fun get(filter: Filter): Flowable<ExchangePoint> =
        findExchangePoint(filter)
            .flatMap { toEntities(it) { toExchangePoint(it) } }

    override fun delete(entity: ExchangePoint): Observable<Int> = super.delete(entity).map {
        databaseOperationProvider.writableDatabase.delete(
            TABLE_NAME,
            "id = ?",
            arrayOf(entity.id)
        )
    }

    private fun findExchangePoint(subjectIds: List<String>): Flowable<Cursor> =
        Flowable.just(
            databaseOperationProvider.readableDatabase.rawQuery(
                "Select exchange_point.id,latitude,longitude,honesty_status,watched_to,exchange_rates_id from exchange_point join watched_subject on exchange_point.watched_subject_id = watched_subject.id where id in( ${
                    mapToQueryParamSymbols(
                        subjectIds
                    )
                })",
                arrayOf(mapToQueryParamVariable(subjectIds))
            )
        )

    private fun findExchangePoint(filter: Filter): Flowable<Cursor> =
        Flowable.just(filter)
            .map { it.subjectFilter.honestyStatusVisibilityMap.filter { it.value } }
            .map { it.keys }
            .map { databaseOperationProvider.readableDatabase.rawQuery(
                "Select exchange_point.id,latitude,longitude,honesty_status,watched_to,exchange_rates_id from exchange_point join watched_subject on exchange_point.watched_subject_id = watched_subject.id where watched_to ='null' and honesty_status in( ${
                    mapToQueryParamSymbols(
                        it
                    )
                }) ",
                arrayOf(mapToQueryParamVariable(it))
            ) }

    private fun toExchangePoint(cursor: Cursor): Flowable<ExchangePoint> =
        Flowable.just(toExchangePointOnly(cursor))
            .flatMap { addSuggestions(it) }
            .flatMap { addExchangeRate(cursor.getString(5), it) }


    private fun addExchangeRate(exchangeRatesId: String?, subject: ExchangePoint) =
        if (exchangeRatesId != null)
            exchangeRateRepository.get(listOf(exchangeRatesId))
                .map { subject.exchangePointRate = it }
                .map { subject }
        else
            Flowable.just(subject)

    private fun toExchangePointOnly(
        cursor: Cursor
    ) = ExchangePoint(
        id = cursor.getString(0),
        position = Position(cursor.getDouble(2), cursor.getDouble(1)),
        honestyStatus = HonestyStatus.valueOf(cursor.getString(3)),
        image = "asfa".toByteArray(),
        suggestions = mutableListOf(),
        exchangePointRate = null,
        watchedTo = getWatchedTo(cursor)
    )

    private fun getWatchedTo(cursor: Cursor) = cursor.getString(4).let {
        if (it == "null")
            null
        else
            LocalDate.parse(it)
    }

    private fun getSuggestions(id: String) =
        Flowable.fromIterable(suggestionRepositories.values)
            .flatMap { it.getBySubjectId(id) }

    private fun addSuggestions(subject: ExchangePoint) =
        getSuggestions(subject.id)
            .map { subject.suggestions.add(it) }
            .map { subject }

    private fun getContentValues(entity: ExchangePoint): ContentValues = ContentValues().apply {
        put("id", entity.id)
        put("watched_subject_id", entity.id)
        put("latitude", entity.position.latitude)
        put("longitude", entity.position.longitude)
        put("exchange_rates_id", entity.exchangePointRate?.id)
    }

    companion object {
        const val TABLE_NAME: String = "exchange_point"
    }

}