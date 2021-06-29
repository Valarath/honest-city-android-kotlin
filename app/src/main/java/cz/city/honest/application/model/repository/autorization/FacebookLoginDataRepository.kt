package cz.city.honest.application.model.repository.autorization

import android.content.ContentValues
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import cz.city.honest.application.model.repository.DatabaseOperationProvider
import cz.city.honest.application.model.repository.user.UserRepository
import cz.city.honest.application.model.service.registration.FacebookLoginData
import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Single

class FacebookLoginDataRepository(
    databaseOperationProvider: DatabaseOperationProvider
) : LoginDataRepository<FacebookLoginData>(databaseOperationProvider) {

    override fun getByUserId(userId: String): Single<FacebookLoginData> =
        findLoginDataByUserId(userId)
            .map {toLoginData(it) }

    private fun toLoginData(cursor: Cursor) = FacebookLoginData(
        facebookUserId = cursor.getString(0),
        accessToken = cursor.getString(2),
        userId = cursor.getString(1)
    )

    private fun findLoginDataByUserId(userId: String): Single<Cursor> =
        Single.just(
            databaseOperationProvider.readableDatabase.rawQuery(
                "Select id, user_id, access_token from facebook_login_data where user_id = ?",
                arrayOf(userId)
            )
        )

    override fun insert(entity: FacebookLoginData): Observable<Long> =
        Observable.just(
            databaseOperationProvider.writableDatabase.insertWithOnConflict(
                TABLE_NAME,
                null,
                getContentValues(entity),
                SQLiteDatabase.CONFLICT_REPLACE
            )
        )

    private fun getContentValues(entity: FacebookLoginData): ContentValues = ContentValues().apply {
        put("id", entity.facebookUserId)
        put("user_id", entity.userId)
        put("access_token", entity.accessToken)
    }

    override fun update(entity: FacebookLoginData): Observable<Int> =
        Observable.just(
            databaseOperationProvider.writableDatabase.update(
                UserRepository.TABLE_NAME,
                getContentValues(entity),
                "where id = ?",
                arrayOf(entity.facebookUserId)
            )
        )

    override fun get(id: List<String>): Flowable<FacebookLoginData> {
        TODO("Not yet implemented")
    }

    override fun delete(entity: FacebookLoginData): Observable<Int> = Observable.just(
        databaseOperationProvider.writableDatabase.delete(
            TABLE_NAME,
            "id = ?",
            arrayOf(entity.facebookUserId)
        )
    )

    companion object {
        const val TABLE_NAME = "facebook_login_data"

    }
}