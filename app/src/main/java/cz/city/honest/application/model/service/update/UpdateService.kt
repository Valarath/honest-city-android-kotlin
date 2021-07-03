package cz.city.honest.application.model.service.update

import cz.city.honest.application.model.service.authorization.AuthorizationService
import io.reactivex.rxjava3.core.Observable

class UpdateService(
    private val privateUpdatableServices: List<PrivateUpdatable>,
    private val publicUpdatableServices: List<PublicUpdatable>,
    private val authorizationService: AuthorizationService
) {

    fun update(): Observable<Unit> =
        Observable.merge(updatePublic(),updatePrivate())

    private fun updatePrivate() = authorizationService
        .getUserToken()
        .toObservable()
        .map { "Bearer $it" }
        .flatMap { updatePrivate(it) }

    private fun updatePrivate(accessToken: String) = Observable
        .fromIterable(privateUpdatableServices)
        .flatMap { it.update(accessToken) }

    private fun updatePublic() = Observable
        .fromIterable(publicUpdatableServices)
        .flatMap { it.update() }

}

interface PrivateUpdatable {

    fun update(accessToken: String): Observable<Unit>

}

interface PublicUpdatable {

    fun update(): Observable<Unit>

}