package cz.city.honest.application.model.repository.subject.exchange

import android.content.ContentValues
import cz.city.honest.application.model.repository.suggestion.exchange.ClosedExchangePointSuggestionRepository
import android.database.Cursor
import cz.city.honest.application.model.dto.Suggestion
import cz.city.honest.application.model.repository.DatabaseOperationProvider
import cz.city.honest.application.model.repository.subject.SubjectRepository
import cz.city.honest.application.model.repository.suggestion.exchange.ExchangeRateSuggestionRepository
import cz.city.honest.application.model.repository.suggestion.SuggestionRepository
import cz.city.honest.mobile.model.dto.ExchangePoint
import cz.city.honest.mobile.model.dto.ExchangeRate
import cz.city.honest.mobile.model.dto.HonestyStatus
import cz.city.honest.mobile.model.dto.Position
import io.reactivex.rxjava3.core.BackpressureStrategy
import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.core.Observable
import java.time.LocalDate

class ExchangePointRepository(
    databaseOperationProvider: DatabaseOperationProvider,
    suggestionRepositories: Map<Class<Suggestion>, SuggestionRepository<Suggestion>>,
    exchangeRateRepository: ExchangeRateRepository
) : SubjectRepository<ExchangePoint>(
    databaseOperationProvider,
    suggestionRepositories,
    exchangeRateRepository
) {
    override fun insert(entity: ExchangePoint): Observable<Long> = super.insert(entity)
        .map {
            databaseOperationProvider.writableDatabase.insert(
                TABLE_NAME,
                "",
                getContentValues(entity)
            )
        }
        .flatMap { exchangeRateRepository.insert(entity.exchangePointRate) }

    override fun update(entity: ExchangePoint): Observable<Int> =
        super.update(entity)
            .map {
                databaseOperationProvider.writableDatabase.update(
                    TABLE_NAME,
                    getContentValues(entity),
                    "where id = ?",
                    arrayOf(entity.id.toString())
                )
            }
            .flatMap { exchangeRateRepository.update(entity.exchangePointRate) }

    override fun get(id: List<Long>): Flowable<ExchangePoint> =
        findExchangePoint(id)
            .flatMap { toEntities(it) { toExchangePoint(it) } }

    fun get(): Flowable<ExchangePoint> =
        findExchangePoint()
            .flatMap { toEntities(it) { toExchangePoint(it) } }

    override fun delete(entity: ExchangePoint): Observable<Int> = super.delete(entity).map {
        databaseOperationProvider.writableDatabase.delete(
            TABLE_NAME,
            "where id = ?",
            arrayOf(entity.id.toString())
        )
    }

    private fun findExchangePoint(subjectIds: List<Long>): Flowable<Cursor> =
        Flowable.just(
            databaseOperationProvider.readableDatabase.rawQuery(
                "Select id integer,latitude,longitude,honesty_level,watched_to,exchange_rates_id from exchange_point where id in( ${
                    mapToQueryParamSymbols(
                        subjectIds
                    )
                })",
                arrayOf(mapToQueryParamVariable(subjectIds))
            )
        )

    private fun findExchangePoint(): Flowable<Cursor> =
        Flowable.just(
            databaseOperationProvider.readableDatabase.rawQuery(
                "Select id integer,latitude,longitude,honesty_level,watched_to,exchange_rates_id from exchange_point",
                arrayOf()
            )
        )

    private fun toExchangePoint(cursor: Cursor): Flowable<ExchangePoint> =
            exchangeRateRepository.get(listOf(cursor.getLong(5)))
            .map { toExchangePoint(cursor, it) }
            .flatMap { addSuggestions(it) }

    private fun toExchangePoint(
        cursor: Cursor,
        exchangeRate: ExchangeRate
    ) = ExchangePoint(
        id = cursor.getLong(0),
        position = Position(cursor.getDouble(2), cursor.getDouble(1)),
        honestyStatus = HonestyStatus.valueOf(cursor.getString(3)),
        image = "asfa".toByteArray(),
        suggestions = mutableListOf(),
        exchangePointRate = exchangeRate,
        watchedTo = LocalDate.parse(cursor.getString(4))
    )

    private fun getClosedExchangePointSuggestion(id: Long) =
        getSuggestionRepository(ClosedExchangePointSuggestionRepository::class.java)
            .getForWatchedSubjects(listOf(id))

    private fun getExchangeRateSuggestion(id: Long) =
        getSuggestionRepository(ExchangeRateSuggestionRepository::class.java)
            .getForWatchedSubjects(listOf(id))

    private fun getSuggestions(id: Long) =
        Flowable.concat(getClosedExchangePointSuggestion(id), getExchangeRateSuggestion(id))

    private fun addSuggestions(subject: ExchangePoint) =
        getSuggestions(subject.id)
            .map { subject.suggestions.add(it) }
            .map { subject }

    private fun getContentValues(entity: ExchangePoint): ContentValues = ContentValues().apply {
        put("id", entity.id)
        put("watched_subject_id", entity.id)
        put("latitude", entity.position.latitude)
        put("longitude", entity.position.longitude)
        put("exchange_rates_id", entity.exchangePointRate.id)
    }

    companion object {
        const val TABLE_NAME: String = "exchange_point"
    }

}