package cz.city.honest.repository.position

import android.content.ContentValues
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import cz.city.honest.dto.Position
import cz.city.honest.repository.DatabaseOperationProvider
import cz.city.honest.repository.Repository
import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Single

class PositionRepository (databaseOperationProvider: DatabaseOperationProvider) :
Repository<Position>(databaseOperationProvider) {

    override fun insert(entity: Position): Observable<Long> = Observable.just(
        databaseOperationProvider.writableDatabase.insertWithOnConflict(
            TABLE_NAME,
            "",
            getContentValues(entity),
            SQLiteDatabase.CONFLICT_REPLACE
        )
    )

    fun get(): Single<Position> =
        Flowable.just(findCurrencySettings())
            .flatMap { toEntities(it) { toCurrencySettings(it) } }
            .firstOrError()

    fun delete(): Observable<Int> = Observable.just(
        databaseOperationProvider.writableDatabase.delete(
            TABLE_NAME,
            null,
            null
        )
    )

    private fun getContentValues(entity: Position) =
        ContentValues().apply {
            put("longitude", entity.longitude)
            put("latitude", entity.latitude)
        }

    private fun toCurrencySettings(cursor: Cursor) = Flowable.just(
        Position(
            longitude = cursor.getDouble(0),
            latitude = cursor.getDouble(1)
        )
    )

    private fun findCurrencySettings() =
        databaseOperationProvider.readableDatabase.rawQuery(
            "Select longitude, latitude from position",
            null
        )

    companion object {
        val TABLE_NAME = "position"
    }


}