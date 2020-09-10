package cz.city.honest.application.viewmodel

import androidx.lifecycle.MutableLiveData
import cz.city.honest.application.model.dto.Suggestion
import cz.city.honest.application.model.service.SuggestionService
import cz.city.honest.application.model.service.UserService
import cz.city.honest.mobile.model.dto.User
import javax.inject.Inject

class UserDetailViewModel @Inject constructor(
    userService: UserService,
    suggestionService: SuggestionService
) : ScheduledViewModel() {

    val userSuggestions: MutableLiveData<List<Suggestion>> =
        MutableLiveData()
    val userData: MutableLiveData<User> = MutableLiveData();

    init {
        schedule {
            getUserData(userService)
            getUserSuggestions(suggestionService)
        }
    }

    private fun getUserSuggestions(suggestionService: SuggestionService) =
        suggestionService.getSuggestionsForUser(userData.value!!.id)
            .subscribe { userSuggestions.postValue(it) }

    private fun getUserData(userService: UserService) =
        userService.getUserData().subscribe { userData.postValue(it) }


}