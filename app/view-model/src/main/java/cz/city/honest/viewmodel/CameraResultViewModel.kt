package cz.city.honest.viewmodel

import androidx.lifecycle.LiveDataReactiveStreams
import cz.city.honest.dto.*
import cz.city.honest.service.authority.AuthorityService
import cz.city.honest.service.position.PositionService
import cz.city.honest.service.suggestion.SuggestionService
import cz.city.honest.service.user.UserService
import cz.city.honest.viewmodel.converter.NewExchangePointSuggestionExchangePointConverter
import io.reactivex.rxjava3.core.BackpressureStrategy
import io.reactivex.rxjava3.core.Observable
import java.time.Instant
import java.util.*
import javax.inject.Inject

class CameraResultViewModel @Inject constructor(
    private val authorityService: AuthorityService,
    private val suggestionService: SuggestionService,
    private val positionService: PositionService,
    private val userService: UserService
) : ScheduledObservableViewModel() {

    val authorityRate = LiveDataReactiveStreams.fromPublisher<ExchangeRate>(getAuthorityRate())
    val loggedUser = LiveDataReactiveStreams.fromPublisher<User>(getUser())

    private fun getUser() =
        scheduleFlowable().flatMap { userService.getUserDataAsMaybe().toFlowable() }

    private fun getAuthorityRate() = scheduleFlowable().flatMap {
        authorityService.getAuthority().toFlowable(BackpressureStrategy.LATEST)
    }

    fun suggestNewRate(subjectId: String?, exchangeRate: ExchangeRate) =
        suggestNewRateSafely(subjectId, exchangeRate)
            .subscribe()

    fun suggestNewSubject(image: String) =
        positionService.getLastKnownUserPosition()
            .map { getNewExchangePointSuggestion(it,image) }
            .flatMapObservable { suggestNewSubject(it) }
            .map { NewExchangePointSuggestionExchangePointConverter.convert(it) }
            .blockingLast()

    private fun suggestNewSubject(suggestion: NewExchangePointSuggestion) =
        suggestionService
            .createSuggestion(suggestion, UserSuggestionStateMarking.NEW)
            .map { suggestion }

    private fun getNewExchangePointSuggestion(position: Position, image: String) =
        NewExchangePointSuggestion(
            id = UUID.randomUUID().toString(),
            state = State.IN_PROGRESS,
            votes = 1,
            position = position,
            subjectId = null,
            createdAt = Instant.now(),
            image = image
        )

    private fun suggestNewRateSafely(subjectId: String?, exchangeRate: ExchangeRate) =
        if (subjectId != null)
            createNewExchangeRateSuggestion(subjectId, exchangeRate)
        else
            Observable.empty()

    private fun createNewExchangeRateSuggestion(subjectId: String, exchangeRate: ExchangeRate) =
        suggestionService
            .createSuggestion(
                getNewExchangeRateSuggestion(subjectId, exchangeRate),
                UserSuggestionStateMarking.NEW
            )

    private fun getNewExchangeRateSuggestion(subjectId: String, exchangeRate: ExchangeRate) =
        ExchangeRateSuggestion(
            id = UUID.randomUUID().toString(),
            state = State.IN_PROGRESS,
            subjectId = subjectId,
            votes = 1,
            suggestedExchangeRate = exchangeRate,
            createdAt = Instant.now()
        )

}