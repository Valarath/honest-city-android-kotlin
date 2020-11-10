package cz.city.honest.application.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import cz.city.honest.application.model.dto.Suggestion
import cz.city.honest.application.model.service.SuggestionService
import cz.city.honest.application.model.service.UserService
import cz.city.honest.mobile.model.dto.User
import javax.inject.Inject

class UserDetailViewModel @Inject constructor(
    val userService: UserService,
    val suggestionService: SuggestionService
) : ScheduledViewModel() {

    val userSuggestions: MutableLiveData<List<Suggestion>> =
        MutableLiveData()
    val userData: MutableLiveData<User> = MutableLiveData();

    init {
        schedule {
            getUserData()
        }
    }

    private fun getUserSuggestions(user: User) = suggestionService.getSuggestionsForUser(user.id)
            .subscribe { userSuggestions.postValue(it) }

    private fun getUserData() =
        userService
            .getUserData()
            .map { subscribeUserData(it) }
            .subscribe { userData.postValue(it)}


    private fun subscribeUserData(user: User):User{
        getUserSuggestions(user)
        return user
    }

}