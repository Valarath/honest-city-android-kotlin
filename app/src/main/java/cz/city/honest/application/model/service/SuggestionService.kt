package cz.city.honest.application.model.service

import cz.city.honest.application.model.dto.ClosedExchangePointSuggestion
import cz.city.honest.application.model.dto.ExchangeRateSuggestion
import cz.city.honest.application.model.dto.State
import cz.city.honest.application.model.dto.Suggestion
import cz.city.honest.mobile.model.dto.ExchangeRate
import cz.city.honest.mobile.model.dto.Watched
import java.time.LocalDate

class SuggestionService {

    fun getSuggestionsForSubject(id: Long): List<Suggestion> = listOf(
        ClosedExchangePointSuggestion(
            Long.MAX_VALUE, State.IN_PROGRESS, 5, id
        ),
        ExchangeRateSuggestion(
            Long.MIN_VALUE,
            state = State.ACCEPTED,
            votes = 10,
            exchangePointId = id,
            suggestedExchangeRate = ExchangeRate(
                55,
                Watched(LocalDate.now(), LocalDate.now()),
                mutableSetOf()
            )
        )
    )

}