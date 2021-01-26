package cz.city.honest.application.model.repository

import android.content.ContentValues
import reactor.core.publisher.Mono
import java.time.Instant
import java.util.*

class UpdateRepository(val databaseOperationProvider: DatabaseOperationProvider) {

    fun addUpdateTime() =
        Mono.just(databaseOperationProvider.writableDatabase.execSQL("delete from system_data"))
            .map { insertSystemData() }

    private fun insertSystemData() =
        databaseOperationProvider
            .writableDatabase
            .insert("system_data", null, ContentValues()
                .apply { put("date_time", Instant.now().toEpochMilli()) }
            )

    fun getLastUpdateTime():Mono<Date> = Mono.just(getNewestSystemData())
        .map { Date.from(Instant.parse(it.getString(1))) }

    private fun getNewestSystemData() = databaseOperationProvider.readableDatabase.rawQuery(
        "SELECT * FROM system_data ORDER BY id DESC LIMIT 1;",
        null
    )
}