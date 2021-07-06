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

    protected fun mapToQueryParamSymbols(objects:List<*>) = objects.joinToString { "?" }

    protected fun mapToQueryParamSymbols(objects:List<*>, prefix:String) = prefixByWhere(prefix,
        objects.joinToString { "?" })

    private fun prefixByWhere(prefix:String, value:String ) =
        if(!value.isNullOrBlank())
            "$prefix($value)"
        else
            value

    protected fun mapToQueryParamVariable(objects:List<*>) = objects.joinToString()

    protected fun getMapParameterArray(objects:List<*>): Array<out String>? =
        if(objects.isNullOrEmpty())
            null
        else
            arrayOf(mapToQueryParamVariable(objects))
}

fun Boolean.toInt() = if (this) 1 else 0
fun Int.toBoolean():Boolean= this==1