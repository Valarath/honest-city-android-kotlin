package cz.city.honest.application.model.service.suggestion

import cz.city.honest.application.model.dto.ClosedExchangePointSuggestion
import cz.city.honest.application.model.dto.Suggestion


interface Updater<TYPE>{

    fun update(subject:TYPE)
}

interface SuggestionUpdater<SUGGESTION_TYPE:Suggestion>:Updater<SUGGESTION_TYPE>

class ClosedExchangePointSuggestionUpdater:SuggestionUpdater<ClosedExchangePointSuggestion> {

    override fun update(subject:ClosedExchangePointSuggestion) {
        TODO("Not yet implemented")
    }
}