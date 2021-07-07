package cz.city.honest.application.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.LiveDataReactiveStreams
import androidx.lifecycle.MutableLiveData
import cz.city.honest.application.model.dto.ExchangeRate
import cz.city.honest.application.model.dto.User
import cz.city.honest.application.model.service.UserService
import cz.city.honest.application.model.service.authority.AuthorityService
import io.reactivex.rxjava3.core.BackpressureStrategy
import javax.inject.Inject

class SubjectDetailViewModel @Inject constructor(authorityService: AuthorityService, val userService: UserService) :
    ScheduledViewModel() {

    val authorityRate : LiveData<ExchangeRate> = LiveDataReactiveStreams.fromPublisher<ExchangeRate> (authorityService.getAuthority().toFlowable(BackpressureStrategy.BUFFER))
    val loggedUser: LiveData<User> = LiveDataReactiveStreams.fromPublisher<User> (getLoggedUser().toFlowable())


    private fun getLoggedUser() = userService.getUserDataAsMaybe()
}