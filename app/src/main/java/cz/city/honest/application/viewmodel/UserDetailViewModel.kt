package cz.city.honest.application.viewmodel

import androidx.lifecycle.LiveDataReactiveStreams
import androidx.lifecycle.Transformations
import cz.city.honest.application.model.dto.Suggestion
import cz.city.honest.application.model.dto.User
import cz.city.honest.application.model.dto.UserSuggestion
import cz.city.honest.application.model.service.UserService
import cz.city.honest.application.model.service.user.UserSuggestionService
import io.reactivex.rxjava3.core.BackpressureStrategy
import javax.inject.Inject

class UserDetailViewModel @Inject constructor(
    val userService: UserService,
    val userSuggestionService: UserSuggestionService
) : ScheduledViewModel() {

    val userData =
        LiveDataReactiveStreams.fromPublisher<User>(getUserData())

    val userSuggestions = Transformations.switchMap(userData){
        LiveDataReactiveStreams.fromPublisher<List<UserSuggestion>>(getUserSuggestions(it).toFlowable())
    }

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