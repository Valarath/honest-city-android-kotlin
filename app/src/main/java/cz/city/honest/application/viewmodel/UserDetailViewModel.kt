package cz.city.honest.application.viewmodel

import androidx.lifecycle.MutableLiveData
import cz.city.honest.application.model.dto.UserSuggestion
import cz.city.honest.application.model.service.UserService
import cz.city.honest.application.model.service.UserSuggestionService
import cz.city.honest.application.model.dto.User
import javax.inject.Inject

class UserDetailViewModel @Inject constructor(
    val userService: UserService,
    val userSuggestionService: UserSuggestionService
) : ScheduledViewModel() {

    val userSuggestions: MutableLiveData<List<UserSuggestion>> =
        MutableLiveData()
    val userData: MutableLiveData<User> = MutableLiveData();

    init {
        schedule {
            getUserData()
        }
    }

    fun deleteSuggestion(suggestion: UserSuggestion) =
        userSuggestionService.delete(suggestion)
            .subscribe()

    private fun getUserSuggestions(user: User) = userSuggestionService.getUserSuggestions(user.id)
        .toList()
        .blockingSubscribe { userSuggestions.postClearValue(it) }

    private fun getUserData() =
        userService
            .getUserData()
            .map { subscribeUserData(it) }
            .subscribe { userData.postClearValue(it) }


    private fun subscribeUserData(user: User): User {
        getUserSuggestions(user)
        return user
    }

}