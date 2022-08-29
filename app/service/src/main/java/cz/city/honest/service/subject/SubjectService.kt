package cz.city.honest.service.subject

import cz.city.honest.dto.Filter
import cz.city.honest.dto.Position
import cz.city.honest.dto.Suggestion
import cz.city.honest.dto.WatchedSubject
import cz.city.honest.service.gateway.external.ExternalSubjectGateway
import cz.city.honest.service.gateway.external.Subjects
import cz.city.honest.service.gateway.internal.InternalSubjectGateway
import cz.city.honest.service.suggestion.SuggestionService
import cz.city.honest.service.update.PublicUpdatable
import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.core.Observable


class SubjectService(
    private val internalSubjectGateway: InternalSubjectGateway,
    private val externalSubjectGateway: ExternalSubjectGateway,
    private val suggestionService: SuggestionService,
    private val positionProvider: PositionProvider
) : PublicUpdatable {

    fun getSubjects(filter: Filter): Flowable<out WatchedSubject> =
        internalSubjectGateway.getSubjects(filter)

    override fun update(): Observable<Unit> =
        positionProvider.provide()
            .firstOrError()
            .flatMapObservable { externalSubjectGateway.getSubjectsInArea(it) }
            .flatMap { update(it) }
    //.onErrorComplete()

    private fun update(it: Subjects) = Observable.concat(
        updateSubjects(it.subjects),
        updateNewSubjectSuggestion(it.newSubjectSuggestions)
    ).map {}

    private fun updateSubjects(subjects: MutableMap<String, List<WatchedSubject>>) =
        Observable.fromIterable(subjects.entries)
            .flatMap { updateSubjects(it.value) }

    private fun updateSubjects(subjects: List<WatchedSubject>) =
        internalSubjectGateway.updateSubjects(subjects)

    private fun updateNewSubjectSuggestion(newSubjectSuggestions: MutableMap<String, List<Suggestion>>) =
        Observable.fromIterable(newSubjectSuggestions.values)
            .flatMap { Observable.fromIterable(it) }
            .flatMap { suggestionService.suggest(it) }

}

interface PositionProvider {

    fun provide(): Observable<Position>

}