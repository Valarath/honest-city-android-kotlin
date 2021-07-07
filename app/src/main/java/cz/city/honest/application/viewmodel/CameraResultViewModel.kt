package cz.city.honest.application.viewmodel

import androidx.lifecycle.LiveDataReactiveStreams
import cz.city.honest.application.model.dto.*
import cz.city.honest.application.model.service.UserService
import cz.city.honest.application.model.service.authority.AuthorityService
import cz.city.honest.application.model.service.suggestion.SuggestionService
import io.reactivex.rxjava3.core.BackpressureStrategy
import java.util.*
import javax.inject.Inject

class CameraResultViewModel @Inject constructor(
    private val authorityService: AuthorityService,
    private val suggestionService: SuggestionService,
    private val userService: UserService
) :
    ScheduledViewModel() {

    val authorityRate  = LiveDataReactiveStreams.fromPublisher<ExchangeRate> ( authorityService.getAuthority().toFlowable(BackpressureStrategy.LATEST))
    val loggedUser = LiveDataReactiveStreams.fromPublisher<User> (getUser().toFlowable())

    private fun getUser() = userService.getUserDataAsMaybe()

    fun suggest(subjectId: String, exchangeRate: ExchangeRate) = suggestionService
        .createSuggestion(
            getNewExchangeRateSuggestion(subjectId, exchangeRate),
            UserSuggestionStateMarking.NEW
        )
        .subscribe()


    private fun getNewExchangeRateSuggestion(subjectId: String, exchangeRate: ExchangeRate) =
        ExchangeRateSuggestion(
            id = UUID.randomUUID().toString(),
            state = State.IN_PROGRESS,
            subjectId =subjectId,
            votes = 1,
            suggestedExchangeRate =exchangeRate
        )

}