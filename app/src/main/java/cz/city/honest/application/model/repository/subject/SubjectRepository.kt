package cz.city.honest.application.model.repository.subject

import android.content.ContentValues
import android.database.Cursor
import cz.city.honest.application.model.dto.Suggestion
import cz.city.honest.application.model.gateway.SubjectGateway
import cz.city.honest.application.model.repository.DatabaseOperationProvider
import cz.city.honest.application.model.repository.ExchangeRateRepository
import cz.city.honest.application.model.repository.Repository
import cz.city.honest.application.model.repository.suggestion.SuggestionRepository
import cz.city.honest.mobile.model.dto.*
import io.reactivex.rxjava3.core.Flowable
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.time.LocalDate
import java.util.*

class SubjectRepository<WATCHED_SUBJECT:WatchedSubject>(
    databaseOperationProvider: DatabaseOperationProvider,
    private val suggestionRepositories:List<SuggestionRepository<out Suggestion>>,
    private val exchangeRateRepository: ExchangeRateRepository
) : Repository<>(databaseOperationProvider){

    private fun subjectPersist(subject: WatchedSubject): Map<Class<out WatchedSubject>, Mono<out WatchedSubject>> =
        mapOf(ExchangePoint::class.java to addExchangePoint(subject as ExchangePoint))

    private fun toExchangePoint(cursor: Cursor): Mono<ExchangePoint> =
        suggestionRepositories.getSubjectSuggestions(cursor.getLong(0))
            .flatMap { suggestions ->
                exchangeRateRepository.getExchangePointRates(cursor.getLong(0))
                    .map { toExchangePoint(cursor, suggestions, it) }
            }

    private fun toExchangePoint(
        cursor: Cursor,
        suggestions: MutableList<Suggestion>,
        it: ExchangeRate
    ): ExchangePoint =
        ExchangePoint(
            id = cursor.getLong(0),
            watchedTo = LocalDate.parse(cursor.getString(1)),
            honestyStatus = HonestyStatus.valueOf(cursor.getString(2)),
            position = Position(cursor.getDouble(3), cursor.getDouble(4)),
            suggestions = suggestions,
            exchangePointRate = it,
            //FIXME from database
            image = "asfa".toByteArray()
        )

    private fun toExchangePoints(cursor: Cursor): Mono<List<ExchangePoint>> =
        Mono.just(ArrayList<ExchangePoint>())
            .repeat { cursor.moveToNext() }
            .flatMap { toExchangePoint(cursor) }
            .collectList()


    fun getSubjects(): Flowable<ExchangePoint> =
        toExchangePoints(
            databaseOperationProvider.readableDatabase.rawQuery(
                "SELECT id,honesty_level,longitude,latitude from exchange_point;",
                null
            )
        )

    fun addSubjects(subjects: List<WatchedSubject>): Mono<List<WatchedSubject>> =
        Mono.just(subjects).flatMapMany { Flux.fromIterable(it) }
            .flatMap { addSubject(it) }
            .collectList()

    private fun addSubject(subject: WatchedSubject): Mono<WatchedSubject> =
        Mono.just(subject)
            .flatMap { subjectPersist(subject)[it::class.java] ?: defaultPersistMethod(it) }

    private fun defaultPersistMethod(subject: WatchedSubject): Mono<WatchedSubject> =
        Mono.just(subject)

    private fun addExchangePoint(subject: ExchangePoint): Mono<ExchangePoint> =
        Mono.just(subject)
            .map { persistExchangePoint(subject) }
            .map { exchangeRateRepository.addExchangeRates(subject) }
            .map { subject }

    private fun persistExchangePoint(subject: ExchangePoint): Mono<Long> {
        return Mono.just(databaseOperationProvider.writableDatabase.insert(
            "exchange_point", null,
            ContentValues()
                .apply {
                    put("id", subject.id)
                    put("latitude", subject.position.latitude)
                    put("longitude", subject.position.longitude)
                    put("honesty_level", subject.honestyStatus.name)
                }
        ))
    }


}