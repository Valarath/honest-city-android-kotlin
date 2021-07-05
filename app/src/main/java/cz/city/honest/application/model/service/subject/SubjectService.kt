package cz.city.honest.application.model.service.subject

import cz.city.honest.application.model.dto.*
import cz.city.honest.application.model.gateway.server.GetSubjectsResponse
import cz.city.honest.application.model.gateway.server.SubjectServerSource
import cz.city.honest.application.model.repository.subject.SubjectRepository
import cz.city.honest.application.model.service.RepositoryProvider
import cz.city.honest.application.model.service.suggestion.SuggestionService
import cz.city.honest.application.model.service.update.PublicUpdatable
import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.core.Observable
import java.time.LocalDate


class SubjectService(
    private val subjectRepositories: Map<String, SubjectRepository<out WatchedSubject>>,
    private val subjectServerSource: SubjectServerSource,
    private val suggestionService: SuggestionService,
    val positionProvider: PositionProvider
) : PublicUpdatable {

    fun getSubjects(): Flowable<out WatchedSubject> =
        Flowable.fromIterable(subjectRepositories.values)
            .map { addFakeSubject() }
    //.flatMap { it.get() }

    private fun addFakeSubject() =
        ExchangePoint(
            "21", LocalDate.now(), HonestyStatus.DISHONEST,
            Position(14.423777, 50.084344),
            mutableListOf(),
            ExchangeRate(
                "", Watched(LocalDate.now(), LocalDate.now()), mutableSetOf(
                    Rate("cze", ExchangeRateValues(1.0)),
                    Rate("eur", ExchangeRateValues(17.0)),
                    Rate("usd", ExchangeRateValues(25.0)),
                    Rate("trr", ExchangeRateValues(22.0))

                )
            ),
            "aaa".toByteArray()
        )

    override fun update(): Observable<Unit> =
        positionProvider.provide()
            .firstOrError()
            .flatMapObservable { subjectServerSource.getSubjectsInArea(toRequest(it)) }
            .flatMap { update(it) }
    //.onErrorComplete()

    private fun update(it: GetSubjectsResponse) = Observable.concat(
        updateSubjects(it.subjects),
        updateNewSubjectSuggestion(it.newSubjectSuggestions)
    ).map {}

    private fun updateSubjects(subjects: MutableMap<String, List<WatchedSubject>>) =
        Observable.fromIterable(subjects.entries)
            .flatMap {
                RepositoryProvider.provide(
                    subjectRepositories,
                    it.key
                ).insertList(it.value)
            }

    private fun updateNewSubjectSuggestion(newSubjectSuggestions: MutableMap<String, List<Suggestion>>) =
        Observable.fromIterable(newSubjectSuggestions.values)
            .flatMap { Observable.fromIterable(it) }
            .flatMap { suggestionService.suggest(it)  }


    private fun toRequest(position: Position) = mapOf(
        "userPosition.latitude" to position.latitude.toString(),
        "userPosition.longitude" to position.longitude.toString()
    )


}

interface PositionProvider {

    fun provide(): Observable<Position>

}