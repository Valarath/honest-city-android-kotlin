package cz.city.honest.application.model.service

import cz.city.honest.application.model.dto.*
import cz.city.honest.mobile.model.dto.*
import io.reactivex.rxjava3.core.Observable
import java.time.LocalDate

class SuggestionService {

    fun getSuggestionsForSubject(id: Long): List<Suggestion> = getMockSuggestions(id)

    private fun getMockSuggestions(id: Long): List<Suggestion> {
        return listOf(
            ClosedExchangePointSuggestion(
                Long.MAX_VALUE, State.IN_PROGRESS, 5, id
            ),
            NewExchangePointSuggestion(
                55,
                state = State.DECLINED,
                votes = 6,
                position = Position(55.0, 77.0)
            ),
            ExchangeRateSuggestion(
                Long.MIN_VALUE,
                state = State.ACCEPTED,
                votes = 10,
                exchangePointId = id,
                suggestedExchangeRate = ExchangeRate(
                    55,
                    Watched(LocalDate.now(), LocalDate.now()),
                    mutableSetOf(
                        Rate("CZK", ExchangeRateValues(22.0)),
                        Rate("USD", ExchangeRateValues(22.0))
                    )
                )
            )
        )
    }

    fun getSuggestionsForUser(id: Long): Observable<List<Suggestion>> = Observable.just(getMockSuggestions(id))

}