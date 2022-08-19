package cz.city.honest.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.LiveDataReactiveStreams
import cz.city.honest.dto.*
import cz.city.honest.service.filter.FilterService
import cz.city.honest.service.subject.PositionProvider
import cz.city.honest.service.subject.SubjectService
import cz.city.honest.service.suggestion.SuggestionService
import cz.city.honest.service.user.UserService
import cz.city.honest.viewmodel.converter.NewExchangePointSuggestionExchangePointConverter
import java.util.*
import javax.inject.Inject

class MapViewModel @Inject constructor(
    private var subjectService: SubjectService,
    private var suggestionService: SuggestionService,
    private var positionProvider: PositionProvider,
    private var userService: UserService,
    private var filterService: FilterService
) : ScheduledObservableViewModel() {

    val watchedSubjects: LiveData<WatchedSubject> = LiveDataReactiveStreams.fromPublisher(getSubjects())
    val newExchangePointSuggestions: LiveData<WatchedSubject> = LiveDataReactiveStreams.fromPublisher(getSuggestionSubjects())
    val loggedUser: LiveData<User> = LiveDataReactiveStreams.fromPublisher<User> (getUser())


    private fun getUser() = scheduleFlowable()
        .flatMap {  userService.getUserDataAsMaybe().toFlowable()}

    fun suggestNewSubject() =
        positionProvider.provide()
            .firstOrError()
            .map { getNewExchangePointSuggestion(it) }
            .flatMapObservable { suggestNewSubject(it) }
            .subscribe { it }

    private fun suggestNewSubject(suggestion: NewExchangePointSuggestion) =
        suggestionService
            .createSuggestion(suggestion, UserSuggestionStateMarking.NEW)
            .map { suggestion }

    private fun getNewExchangePointSuggestion(position: Position) =
        NewExchangePointSuggestion(
            id = UUID.randomUUID().toString(),
            state = State.IN_PROGRESS,
            votes = 1,
            position = position,
            subjectId = null
        )

    private fun getSubjects() =  scheduleFlowable()
        .flatMap { filterService.getFilter().toFlowable() }
        .flatMap { subjectService.getSubjects(it) }
        .onBackpressureBuffer(1000)

    private fun getSuggestionSubjects() =
        scheduleFlowable().flatMap {suggestionService.getSuggestions(NewExchangePointSuggestion::class.java)  }
            .map { toExchangePoint(it) }
            .onBackpressureBuffer(1000)

    private fun toExchangePoint(newExchangePointSuggestion: NewExchangePointSuggestion): WatchedSubject =
        NewExchangePointSuggestionExchangePointConverter.convert(newExchangePointSuggestion)

}
