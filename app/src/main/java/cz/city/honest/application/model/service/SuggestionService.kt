package cz.city.honest.application.model.service

import cz.city.honest.application.model.dto.*
import cz.city.honest.application.model.gateway.server.PostSuggestRequest
import cz.city.honest.application.model.gateway.server.RemoveSuggestionRequest
import cz.city.honest.application.model.gateway.server.SuggestionServerSource
import cz.city.honest.application.model.repository.user.UserSuggestionRepository
import cz.city.honest.mobile.model.dto.*
import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.core.Observable
import java.time.LocalDate
import java.util.*

class SuggestionService(
    val suggestionServerSource: SuggestionServerSource,
    val userSuggestionRepository: UserSuggestionRepository,
    val userService: UserService
) : Updatable {

    fun getSuggestionsForSubject(id: String): Observable<Suggestion> =
        Observable.fromIterable(getMockSuggestions(id))

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
                    55,
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

    fun getSuggestionsForUser(id: String): Observable<Suggestion> =
        Observable.fromIterable(getMockSuggestions(id))

    override fun update(): Observable<Unit> =
        userService.getUserData()
            .concatMap { removeSuggestions(it);suggestSuggestions(it) }

    private fun removeSuggestions(user: User): Observable<Unit> =
        userSuggestionRepository.getForDelete(listOf(user.id))
            .toList()
            .toObservable()
            .flatMap { removeSuggestions(it) }
            .map {}

    private fun removeSuggestions(userSuggestions: MutableList<UserSuggestion>) =
        Flowable.fromIterable(userSuggestions)
            .map { it.suggestion }
            .toList()
            .toObservable()
            .flatMap { suggestionServerSource.remove(RemoveSuggestionRequest(it)) }
            .flatMap { userSuggestionRepository.deleteList(userSuggestions) }

    private fun suggestSuggestions(user: User): Observable<Unit> =
        userSuggestionRepository.getNew(listOf(user.id))
            .toList()
            .toObservable()
            .flatMap { suggestSuggestions(it) }
            .map {}

    private fun suggestSuggestions(userSuggestions: MutableList<UserSuggestion>) =
        Flowable.fromIterable(userSuggestions)
            .map { it.suggestion }
            .toList()
            .toObservable()
            .flatMap { suggestionServerSource.suggest(PostSuggestRequest(it)) }
            .flatMap { userSuggestionRepository.updateList(userSuggestions) }

}