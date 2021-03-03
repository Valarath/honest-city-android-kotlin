package cz.city.honest.application.model.service

import cz.city.honest.application.model.dto.*
import cz.city.honest.application.model.gateway.server.SuggestionServerSource
import cz.city.honest.application.model.repository.suggestion.SuggestionRepository
import cz.city.honest.mobile.model.dto.*
import io.reactivex.rxjava3.core.Observable
import java.time.LocalDate

class SuggestionService(val suggestionServerSource: SuggestionServerSource, val suggestionRepositories:Map<Class<out Suggestion>,SuggestionRepository<Suggestion>>) {

    fun getSuggestionsForSubject(id: Long): Observable<Suggestion> = Observable.fromIterable(getMockSuggestions(id))

    private fun getMockSuggestions(id: Long): List<Suggestion> {
        return listOf(
            ClosedExchangePointSuggestion(
                Long.MAX_VALUE, State.IN_PROGRESS, 5, id,Long.MAX_VALUE
            ),
            NewExchangePointSuggestion(
                55,
                state = State.DECLINED,
                votes = 6,
                position = Position(55.0, 77.0),
                suggestionId = 55
            ),
            ExchangeRateSuggestion(
                Long.MIN_VALUE,
                state = State.ACCEPTED,
                votes = 10,
                watchedSubjectId = id,
                suggestedExchangeRate = ExchangeRate(
                    55,
                    Watched(LocalDate.now(), LocalDate.now()),
                    mutableSetOf(
                        Rate("CZK", ExchangeRateValues(22.0)),
                        Rate("USD", ExchangeRateValues(22.0))
                    )
                ),
                suggestionId = Long.MAX_VALUE
            )
        )
    }

    fun getSuggestionsForUser(id: Long): Observable<Suggestion> = Observable.fromIterable(getMockSuggestions(id))

}