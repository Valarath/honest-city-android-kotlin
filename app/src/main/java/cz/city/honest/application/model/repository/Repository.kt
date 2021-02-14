package cz.city.honest.application.model.repository

import android.database.Cursor
import androidx.core.database.sqlite.transaction
import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.core.Observable

abstract class Repository <ENTITY>(protected val databaseOperationProvider: DatabaseOperationProvider){

    protected fun toEntities(
        cursor: Cursor, toSuggestion: (cursor: Cursor) -> Flowable<ENTITY>
    ): Flowable<ENTITY> = Flowable.just(cursor)
        .repeatUntil { cursor.moveToNext() }
        .flatMap { toSuggestion(it) }
        .doFinally { cursor.close() }

    protected fun processListInTransaction(
        list: List<ENTITY>,
        process: (listMember: ENTITY) -> Observable<*>
    ) = Observable.just(databaseOperationProvider.writableDatabase.transaction {
        list.forEach { process.invoke(it) }
    })

}