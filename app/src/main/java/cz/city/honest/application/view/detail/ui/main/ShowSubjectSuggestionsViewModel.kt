package cz.city.honest.application.view.detail.ui.main

import cz.city.honest.application.model.dto.Suggestion
import cz.city.honest.application.model.service.SuggestionService
import cz.city.honest.application.model.service.vote.VoteService
import cz.city.honest.application.viewmodel.ScheduledViewModel
import io.reactivex.rxjava3.core.Observable
import javax.inject.Inject

class ShowSubjectSuggestionsViewModel @Inject constructor(
    var suggestionService: SuggestionService,
    var voteService: VoteService
) : ScheduledViewModel() {

    fun voteFor(suggestion: Suggestion, subjectId: String) =
        voteService.vote(suggestion)
            .map { getSuggestionsForSubject(subjectId) }
            .subscribe()

    fun getSuggestionsForSubject(subjectId: String) =
        Observable.merge(getVotedSuggestionsForSubject(subjectId),getUnvotedSuggestionsForSubject(subjectId))
            .toList()
            .blockingGet()

    private fun getVotedSuggestionsForSubject(subjectId: String) =
        voteService.getVotesForSubject(subjectId)
            .map { VotedSuggestion(it.suggestion,true) }


    private fun getUnvotedSuggestionsForSubject(subjectId: String) =
        suggestionService.getSuggestionsForSubject(subjectId)
            .map { VotedSuggestion(it,false) }

}

data class VotedSuggestion(val suggestion: Suggestion, val voted: Boolean)