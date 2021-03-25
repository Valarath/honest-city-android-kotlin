package cz.city.honest.application.model.repository

import android.database.Cursor
import androidx.core.database.sqlite.transaction
import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.core.Observable

abstract class Repository <ENTITY>(protected val databaseOperationProvider: DatabaseOperationProvider){

    abstract fun insert(entity: ENTITY): Observable<Long>
    abstract fun update(entity: ENTITY): Observable<Int>
    abstract fun get(id:List<String>):Flowable<ENTITY>
    abstract fun delete(entity: ENTITY): Observable<Int>

    fun insertList(entities: List<ENTITY>) =
        processListInTransaction(entities, ::insert)

    fun updateList(entities: List<ENTITY>) =
        processListInTransaction(entities, ::update)

    protected fun toEntities(
        cursor: Cursor, toEntity: (cursor: Cursor) -> Flowable<ENTITY>
    ): Flowable<ENTITY> = Flowable.just(cursor)
        .repeatUntil { cursor.moveToNext() }
        .flatMap { toEntity(it) }
        .doFinally { cursor.close() }

    protected fun processListInTransaction(
        list: List<ENTITY>,
        process: (listMember: ENTITY) -> Observable<*>
    ) = Observable.just(databaseOperationProvider.writableDatabase.transaction {
        list.forEach { process.invoke(it) }
    })

    protected fun mapToQueryParamSymbols(objects:List<*>) = objects.joinToString { "?" }

    protected fun mapToQueryParamVariable(objects:List<*>) = objects.joinToString()
}

fun Int.toBoolean():Boolean= this==1