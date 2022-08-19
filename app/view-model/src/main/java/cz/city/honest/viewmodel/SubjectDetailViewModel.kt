package cz.city.honest.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.LiveDataReactiveStreams
import cz.city.honest.dto.ExchangeRate
import cz.city.honest.dto.User
import cz.city.honest.service.user.UserService
import io.reactivex.rxjava3.core.BackpressureStrategy
import javax.inject.Inject

class SubjectDetailViewModel @Inject constructor(authorityService: cz.city.honest.service.authority.AuthorityService, val userService: UserService) :
    ScheduledObservableViewModel() {

    val authorityRate : LiveData<ExchangeRate> = LiveDataReactiveStreams.fromPublisher<ExchangeRate> (authorityService.getAuthority().toFlowable(BackpressureStrategy.BUFFER))
    val loggedUser: LiveData<User> = LiveDataReactiveStreams.fromPublisher<User> (getLoggedUser().toFlowable())


    private fun getLoggedUser() = userService.getUserDataAsMaybe()
}