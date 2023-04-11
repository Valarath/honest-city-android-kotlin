package cz.city.honest.repository.settings

import android.content.ContentValues
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import com.fasterxml.jackson.databind.ObjectMapper
import cz.city.honest.dto.SubjectSettings
import cz.city.honest.repository.DatabaseOperationProvider
import cz.city.honest.repository.Repository
import cz.city.honest.service.gateway.internal.InternalSubjectSettingsGateway
import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Single

class SubjectSettingsRepository(
    databaseOperationProvider: DatabaseOperationProvider,
    private val objectMapper: ObjectMapper
) : Repository<SubjectSettings>(databaseOperationProvider) {

    fun get(): Single<SubjectSettings> = Flowable.just(findExchangeNameSettings())
        .flatMap { toEntities(it) { toName(it) } }
        .firstOrError()

    override fun insert(entity: SubjectSettings): Observable<Long> =
        Observable.just(
            databaseOperationProvider.writableDatabase.insertWithOnConflict(
                TABLE_NAME,
                "",
                getContentValues(entity),
                SQLiteDatabase.CONFLICT_REPLACE
            )
        )

    fun delete(): Observable<Int> = Observable.just(
        databaseOperationProvider.writableDatabase.delete(
            TABLE_NAME,
            null,
            null
        )
    )

    private fun toName(cursor: Cursor) =
        Flowable.just(
            objectMapper.readValue(cursor.getString(0), SubjectSettings::class.java)
        )

    private fun findExchangeNameSettings() =
        databaseOperationProvider.readableDatabase.rawQuery(
            "Select data from subject_settings",
            null
        )

    private fun getContentValues(entity: SubjectSettings) =
        ContentValues().apply {
            put("data", objectMapper.writeValueAsString(entity))
        }

    companion object {
        val TABLE_NAME = "subject_settings"
    }
}

class SubjectSettingsService(private val subjectSettingsRepository: SubjectSettingsRepository) :
    InternalSubjectSettingsGateway {

    override fun get(): Single<SubjectSettings> = subjectSettingsRepository.get()

    override fun insert(settings: SubjectSettings): Single<Unit> =
        subjectSettingsRepository.insert(settings)
            .toList()
            .map { }

    override fun delete(): Observable<Unit> = subjectSettingsRepository.delete()
        .map { }
}