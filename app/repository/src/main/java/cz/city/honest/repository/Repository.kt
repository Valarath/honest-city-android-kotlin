package cz.city.honest.repository

import android.database.Cursor
import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.core.Observable

abstract class Repository<ENTITY>(protected val databaseOperationProvider: DatabaseOperationProvider) {

    abstract fun insert(entity: ENTITY): Observable<Long>

    protected fun toEntities(
        cursor: Cursor, toEntity: (cursor: Cursor) -> Flowable<ENTITY>
    ): Flowable<ENTITY> =
        Flowable.generate {
            if (cursorContainsData(cursor))
                it.onNext(toEntity(cursor).blockingFirst())
            else
                it.onComplete()
                    .also { cursor.close() }
        }

    protected fun cursorContainsData(cursor: Cursor) =
        cursor.moveToNext() && !cursor.isClosed

    protected fun isCursorNotEmpty(it: Cursor) =
        it.count > 0

    protected fun mapToQueryParamSymbols(objects: Collection<*>) =
        objects.joinToString { "?" }

    protected fun mapToQueryParamVariable(objects: Collection<*>) =
        objects.joinToString()

    protected fun getMapParameterArray(objects: List<*>): Array<out String>? =
        if (objects.isNullOrEmpty())
            null
        else
            arrayOf(mapToQueryParamVariable(objects))

    protected fun getClassForName(className: String):Class<ENTITY> = Class.forName(className) as Class<ENTITY>
}

fun Boolean.toInt() = if (this) 1 else 0
fun Int.toBoolean(): Boolean = this == 1