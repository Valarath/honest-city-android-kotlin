package cz.city.honest.viewmodel

import androidx.lifecycle.LiveDataReactiveStreams
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import cz.city.honest.dto.Suggestion
import cz.city.honest.dto.User
import cz.city.honest.dto.UserSuggestionStateMarking
import cz.city.honest.service.user.UserService
import io.reactivex.rxjava3.core.Observable
import javax.inject.Inject

class ShowSubjectSuggestionsViewModel @Inject constructor(
    private var suggestionService: cz.city.honest.service.suggestion.SuggestionService,
    private var voteService: cz.city.honest.service.vote.VoteService,
    private var userService: UserService
) : ScheduledObservableViewModel() {

    val subjectId: MutableLiveData<String> = MutableLiveData()
    val loggedUser = LiveDataReactiveStreams.fromPublisher<User>(getUser())

    val subjectSuggestions = Transformations.switchMap(subjectId) {
        LiveDataReactiveStreams.fromPublisher<List<VotedSuggestion>>(
            getScheduledSuggestionsForSubject(it)
        )
    }

    private fun getUser() =
        scheduleFlowable().flatMap { userService.getUserDataAsMaybe().toFlowable() }

    fun voteFor(suggestion: Suggestion) =
        voteService.vote(suggestion)
            .subscribe()

    private fun getScheduledSuggestionsForSubject(subjectId: String) =
        scheduleFlowable()
            .flatMap { getSuggestionsForSubject(subjectId) }

    private fun getSuggestionsForSubject(subjectId: String) =
        Observable.merge(
            getVotedSuggestionsForSubject(subjectId),
            getUnvotedSuggestionsForSubject(subjectId)
        )
            .toList()
            .toFlowable()

    fun suggest(suggestion: Suggestion, markAs: UserSuggestionStateMarking, subjectId: String) =
        suggestionService.createSuggestion(suggestion, markAs)
            //.map { getSuggestionsForSubject(subjectId) }
            .subscribe()

    private fun getVotedSuggestionsForSubject(subjectId: String) =
        voteService.getVotesForSubject(subjectId)
            .map { VotedSuggestion(it.suggestion, true) }


    private fun getUnvotedSuggestionsForSubject(subjectId: String) =
        suggestionService.getUnvotedSuggestionsForSubject(subjectId)
            .map { VotedSuggestion(it, false) }

}

data class VotedSuggestion(val suggestion: Suggestion, val voted: Boolean)