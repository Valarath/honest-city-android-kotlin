package cz.city.honest.application.model.service

import cz.city.honest.application.model.dto.*
import cz.city.honest.application.model.gateway.server.PostSuggestRequest
import cz.city.honest.application.model.gateway.server.RemoveSuggestionRequest
import cz.city.honest.application.model.gateway.server.SuggestionServerSource
import cz.city.honest.application.model.repository.suggestion.SuggestionRepository
import cz.city.honest.application.model.repository.user.UserSuggestionRepository
import cz.city.honest.mobile.model.dto.*
import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.core.Observable
import java.time.LocalDate
import java.util.*

class SuggestionService(val suggestionRepositories: Map<String, @JvmSuppressWildcards SuggestionRepository<out Suggestion>>, val userSuggestionRepository: UserSuggestionRepository) {

    fun getSuggestionsForSubject(id: String): Observable<Suggestion> =
        Observable.fromIterable(getMockSuggestions(id))

    fun <SUGGESTION_TYPE:Suggestion>getSuggestions(clazz: Class<SUGGESTION_TYPE>):Flowable<out SUGGESTION_TYPE> = (RepositoryProvider.provide(suggestionRepositories,clazz) as SuggestionRepository<SUGGESTION_TYPE>).get()

    private fun getMockSuggestions(id: String): List<Suggestion> {
        return listOf(
            ClosedExchangePointSuggestion(
                UUID.randomUUID().toString(),
                State.IN_PROGRESS,
                5,
                UUID.randomUUID().toString(),
                UUID.randomUUID().toString()
            ),
            NewExchangePointSuggestion(
                UUID.randomUUID().toString(),
                state = State.DECLINED,
                votes = 6,
                position = Position(55.0, 77.0),
                suggestionId = UUID.randomUUID().toString()
            ),
            ExchangeRateSuggestion(
                UUID.randomUUID().toString(),
                state = State.ACCEPTED,
                votes = 10,
                watchedSubjectId = UUID.randomUUID().toString(),
                suggestedExchangeRate = ExchangeRate(
                    "",
                    Watched(LocalDate.now(), LocalDate.now()),
                    mutableSetOf(
                        Rate("CZK", ExchangeRateValues(22.0)),
                        Rate("USD", ExchangeRateValues(22.0))
                    )
                ),
                suggestionId = UUID.randomUUID().toString()
            )
        )
    }

    fun update(suggestion: Suggestion) =
        RepositoryProvider.provide(suggestionRepositories, suggestion::class.java)
            .update(updateSuggestion(suggestion))

    private fun updateSuggestion(suggestion: Suggestion) = suggestion.apply { this.increaseVotes() }

}