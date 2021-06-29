package cz.city.honest.application.view.detail.ui.main

import androidx.lifecycle.MutableLiveData
import cz.city.honest.application.model.dto.Suggestion
import cz.city.honest.application.model.dto.User
import cz.city.honest.application.model.dto.UserSuggestionStateMarking
import cz.city.honest.application.model.service.UserService
import cz.city.honest.application.model.service.suggestion.SuggestionService
import cz.city.honest.application.model.service.vote.VoteService
import cz.city.honest.application.viewmodel.ScheduledViewModel
import cz.city.honest.application.viewmodel.postClearValue
import io.reactivex.rxjava3.core.Observable
import javax.inject.Inject

class ShowSubjectSuggestionsViewModel @Inject constructor(
    private var suggestionService: SuggestionService,
    private var voteService: VoteService,
    private var userService: UserService
) : ScheduledViewModel() {

    var loggedUser: User? = null

    init {
        schedule {
            getLoggedUser().subscribe({ loggedUser = it }, {}, { loggedUser = null })
        }
    }

    private fun getLoggedUser() = userService.getUserDataAsMaybe()

    fun voteFor(suggestion: Suggestion, subjectId: String) =
        voteService.vote(suggestion)
            .map { getSuggestionsForSubject(subjectId) }
            .subscribe()

    //TODO ADD this  as mutableList liveData - it is possible to do!!
    fun getSuggestionsForSubject(subjectId: String) =
        Observable.merge(
            getVotedSuggestionsForSubject(subjectId),
            getUnvotedSuggestionsForSubject(subjectId)
        )
            .toList()
            .blockingGet()

    fun suggest(suggestion: Suggestion, markAs: UserSuggestionStateMarking) =
        suggestionService.suggest(suggestion, markAs)
            .subscribe()

    private fun getVotedSuggestionsForSubject(subjectId: String) =
        voteService.getVotesForSubject(subjectId)
            .map { VotedSuggestion(it.suggestion, true) }


    private fun getUnvotedSuggestionsForSubject(subjectId: String) =
        suggestionService.getSuggestionsForSubject(subjectId)
            .map { VotedSuggestion(it, false) }

}

data class VotedSuggestion(val suggestion: Suggestion, val voted: Boolean)