package cz.city.honest.application.view.detail.ui.main

import cz.city.honest.application.model.service.SuggestionService
import cz.city.honest.application.viewmodel.ScheduledViewModel
import javax.inject.Inject

class ShowSubjectSuggestionsViewModel @Inject constructor(
    var suggestionService: SuggestionService
) : ScheduledViewModel(){

    fun getSuggestionsForSubject(subjectId:String) = suggestionService.getSuggestionsForSubject(subjectId)
}