package cz.city.honest.application.viewmodel

import androidx.lifecycle.MutableLiveData
import cz.city.honest.application.model.dto.*
import cz.city.honest.application.model.service.*
import cz.city.honest.application.model.service.subject.PositionProvider
import cz.city.honest.application.model.service.subject.SubjectService
import cz.city.honest.application.model.service.suggestion.SuggestionService
import cz.city.honest.application.viewmodel.converter.NewExchangePointSuggestionExchangePointConverter
import io.reactivex.rxjava3.core.Flowable
import java.util.*
import javax.inject.Inject

class MapViewModel @Inject constructor(
    private var subjectService: SubjectService,
    private var suggestionService: SuggestionService,
    private var positionProvider: PositionProvider,
    private var userService: UserService
) : ScheduledViewModel() {

    val watchedSubjects: MutableLiveData<List<WatchedSubject>> = MutableLiveData()
    val loggedUser: MutableLiveData<User> = MutableLiveData()

    init {
        schedule {
            getSubjects().subscribe { watchedSubjects.postClearValue(it) }
            getUser().subscribe { loggedUser.postClearValue(it) }
        }
    }

    private fun getUser() = userService.getUserDataAsMaybe()

    fun suggestNewSubject() =
        positionProvider.provide()
            .firstOrError()
            .map { getNewExchangePointSuggestion(it) }
            .flatMapObservable { suggestNewSubject(it) }
            .map { addWatchedSubject(it) }
            .subscribe { it }

    private fun addWatchedSubject(suggestion: NewExchangePointSuggestion) =
        toExchangePoint(suggestion)
            .apply { watchedSubjects.postClearValue(addWatchedSubject(this)) }

    private fun addWatchedSubject(watchedSubject: WatchedSubject) =
        watchedSubjects.value!!
            .toMutableList()
            .apply { add(watchedSubject) }


    private fun suggestNewSubject(suggestion: NewExchangePointSuggestion) =
        suggestionService
            .suggest(suggestion, UserSuggestionStateMarking.NEW)
            .map { suggestion }

    private fun getNewExchangePointSuggestion(position: Position) =
        NewExchangePointSuggestion(
            id = UUID.randomUUID().toString(),
            state = State.IN_PROGRESS,
            votes = 1,
            position = position
        )

    private fun getSubjects() =
        Flowable.merge(subjectService.getSubjects(), getSuggestionSubjects())
            .toList()
            .toObservable()

    private fun getSuggestionSubjects() =
        suggestionService.getSuggestions(NewExchangePointSuggestion::class.java)
            .map { toExchangePoint(it) }

    private fun toExchangePoint(newExchangePointSuggestion: NewExchangePointSuggestion) =
        NewExchangePointSuggestionExchangePointConverter.convert(newExchangePointSuggestion)

}
