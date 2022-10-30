package cz.city.honest.viewmodel

import androidx.lifecycle.LiveDataReactiveStreams
import androidx.lifecycle.Transformations
import cz.city.honest.dto.LoginData
import cz.city.honest.dto.User
import cz.city.honest.dto.UserSuggestion
import cz.city.honest.service.authorization.AuthorizationService
import cz.city.honest.service.user.UserService
import io.reactivex.rxjava3.core.BackpressureStrategy
import javax.inject.Inject

class UserDetailViewModel @Inject constructor(
    val userService: UserService,
    private val authorizationService: AuthorizationService,
    private val userSuggestionService: cz.city.honest.service.user.UserSuggestionService
) : ScheduledObservableViewModel() {

    val userData =
        LiveDataReactiveStreams.fromPublisher<User>(getUserData())

    val userSuggestions = Transformations.switchMap(userData){
        LiveDataReactiveStreams.fromPublisher<List<UserSuggestion>>(getUserSuggestions(it).toFlowable())
    }

    fun logout() = authorizationService.logout()
        .subscribe()

    fun deleteSuggestion(suggestion: UserSuggestion) =
        userSuggestionService.delete(suggestion)
            .subscribe()

    private fun getUserSuggestions(
        user: User
    ) = userSuggestionService.getUserSuggestions(user.id)
        .toList()

    private fun getUserData() = scheduleFlowable()
        .flatMap { userService.getLoggedUser().toFlowable(BackpressureStrategy.LATEST) }

}