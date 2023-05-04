package cz.city.honest.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.LiveDataReactiveStreams
import cz.city.honest.dto.NewExchangePointSuggestion
import cz.city.honest.dto.Position
import cz.city.honest.dto.User
import cz.city.honest.dto.WatchedSubject
import cz.city.honest.service.filter.FilterService
import cz.city.honest.service.position.PositionService
import cz.city.honest.service.subject.SubjectService
import cz.city.honest.service.suggestion.SuggestionService
import cz.city.honest.service.user.UserService
import cz.city.honest.viewmodel.converter.NewExchangePointSuggestionExchangePointConverter
import javax.inject.Inject

class MapViewModel @Inject constructor(
    private var subjectService: SubjectService,
    private var suggestionService: SuggestionService,
    private var userService: UserService,
    private var filterService: FilterService,
    private var positionService: PositionService
) : ScheduledObservableViewModel() {

    val watchedSubjects: LiveData<WatchedSubject> = LiveDataReactiveStreams.fromPublisher(getSubjects())
    val newExchangePointSuggestions: LiveData<WatchedSubject> = LiveDataReactiveStreams.fromPublisher(getSuggestionSubjects())
    val loggedUser: LiveData<User> = LiveDataReactiveStreams.fromPublisher<User> (getUser())

    fun saveCurrentPosition(position: Position) =
        positionService
        .saveCurrentUserPosition(position)
        .subscribe()

    private fun getUser() = scheduleFlowable()
        .flatMap {  userService.getUserDataAsMaybe().toFlowable()}

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
