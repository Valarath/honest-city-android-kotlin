package cz.city.honest.application.viewmodel

import androidx.lifecycle.LiveDataReactiveStreams
import cz.city.honest.application.model.dto.Suggestion
import cz.city.honest.application.model.dto.User
import cz.city.honest.application.model.dto.UserSuggestionStateMarking
import cz.city.honest.application.model.service.UserService
import cz.city.honest.application.model.service.suggestion.SuggestionService
import cz.city.honest.application.model.service.vote.VoteService
import io.reactivex.rxjava3.core.BackpressureStrategy
import io.reactivex.rxjava3.core.Observable
import javax.inject.Inject

class ShowSubjectSuggestionsViewModel @Inject constructor(
    private var suggestionService: SuggestionService,
    private var voteService: VoteService,
    private var userService: UserService
) : ScheduledViewModel() {

    var loggedUser= LiveDataReactiveStreams.fromPublisher<User> (getUser())

    private fun getUser() =
        scheduleFlowable().flatMap { userService.getUserDataAsMaybe().toFlowable() }

    fun voteFor(suggestion: Suggestion, subjectId: String) =
        voteService.vote(suggestion)
            .map { getSuggestionsForSubject(subjectId) }
            .subscribe()

    fun getSuggestionsForSubject(subjectId: String) =
        Observable.merge(
            getVotedSuggestionsForSubject(subjectId),
            getUnvotedSuggestionsForSubject(subjectId)
        )
            .toList()
            .blockingGet()

    fun suggest(suggestion: Suggestion, markAs: UserSuggestionStateMarking, subjectId: String) =
        suggestionService.createSuggestion(suggestion, markAs)
            .map { getSuggestionsForSubject(subjectId) }
            .subscribe()

    private fun getVotedSuggestionsForSubject(subjectId: String) =
        voteService.getVotesForSubject(subjectId)
            .map { VotedSuggestion(it.suggestion, true) }


    private fun getUnvotedSuggestionsForSubject(subjectId: String) =
        suggestionService.getUnvotedSuggestionsForSubject(subjectId)
            .map { VotedSuggestion(it, false) }

}

data class VotedSuggestion(val suggestion: Suggestion, val voted: Boolean)