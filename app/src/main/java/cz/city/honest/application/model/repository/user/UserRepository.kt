package cz.city.honest.application.model.repository.user

import cz.city.honest.application.model.repository.DatabaseOperationProvider
import cz.city.honest.application.model.repository.Repository
import cz.city.honest.mobile.model.dto.User
import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.core.Observable

class UserRepository(operationProvider: DatabaseOperationProvider):Repository<User>(operationProvider) {

    override fun insert(entity: User): Observable<Long> {
        TODO("Not yet implemented")
    }

    override fun update(entity: User): Observable<Int> {
        TODO("Not yet implemented")
    }

    override fun get(id: List<Long>): Flowable<User> {
        TODO("Not yet implemented")
    }

    override fun delete(entity: User): Observable<Int> {
        TODO("Not yet implemented")
    }
}