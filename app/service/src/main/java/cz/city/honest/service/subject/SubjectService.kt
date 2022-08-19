package cz.city.honest.service.subject

import cz.city.honest.dto.Filter
import cz.city.honest.dto.Position
import cz.city.honest.dto.Suggestion
import cz.city.honest.dto.WatchedSubject
import cz.city.honest.external.GetSubjectsResponse
import cz.city.honest.external.SubjectServerSource
import cz.city.honest.service.RepositoryProvider
import cz.city.honest.service.suggestion.SuggestionService
import cz.city.honest.service.update.PublicUpdatable
import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.core.Observable


class SubjectService(
    private val subjectRepositories: Map<String, cz.city.honest.repository.subject.SubjectRepository<out WatchedSubject>>,
    private val subjectServerSource: SubjectServerSource,
    private val suggestionService: SuggestionService,
    private val positionProvider: PositionProvider
) : PublicUpdatable {

    fun getSubjects(filter: Filter): Flowable<out WatchedSubject> =
        Flowable.fromIterable(subjectRepositories.values)
            .flatMap { it.get(filter) }

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
            .flatMap { updateSubjects(it.value) }

    private fun updateSubjects(subjects: List<WatchedSubject>) =
        Observable.fromIterable(subjects)
            .flatMap {
                RepositoryProvider.provide(subjectRepositories, it.getClassName()).insert(it)
            }


    private fun updateNewSubjectSuggestion(newSubjectSuggestions: MutableMap<String, List<Suggestion>>) =
        Observable.fromIterable(newSubjectSuggestions.values)
            .flatMap { Observable.fromIterable(it) }
            .flatMap { suggestionService.suggest(it) }


    private fun toRequest(position: Position) = mapOf(
        "userPosition.latitude" to position.latitude.toString(),
        "userPosition.longitude" to position.longitude.toString()
    )


}

interface PositionProvider {

    fun provide(): Observable<Position>

}