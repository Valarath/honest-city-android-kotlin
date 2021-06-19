package cz.city.honest.application.model.repository.user

import android.database.Cursor
import cz.city.honest.application.model.repository.DatabaseOperationProvider
import cz.city.honest.application.model.repository.Repository
import cz.city.honest.mobile.model.dto.User
import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Single

class UserRepository(operationProvider: DatabaseOperationProvider) :
    Repository<User>(operationProvider) {

    override fun insert(entity: User): Observable<Long> {
        TODO("Not yet implemented")
    }

    override fun update(entity: User): Observable<Int> {
        TODO("Not yet implemented")
    }

    override fun get(id: List<String>): Flowable<User> {
        TODO("Not yet implemented")
    }

    fun getLoggedUser(): Single<User> =
        findLoggedUser()
            .map { toUser(it) }

    private fun toUser(cursor: Cursor): User {
        return User(
            id = cursor.getString(0),
            username = cursor.getString(2),
            score = cursor.getInt(1)
        )
    }

    //fun loginUser(user: User)=

    private fun findLoggedUser(): Single<Cursor> =
        Single.just(
            databaseOperationProvider.readableDatabase.rawQuery(
                "Select id, score, username from user where logged",
                arrayOf()
            )
        )


    override fun delete(entity: User): Observable<Int> {
        TODO("Not yet implemented")
    }
}