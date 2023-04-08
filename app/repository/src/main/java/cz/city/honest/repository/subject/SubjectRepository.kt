package cz.city.honest.repository.subject

import android.content.ContentValues
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import com.fasterxml.jackson.databind.ObjectMapper
import cz.city.honest.dto.Filter
import cz.city.honest.dto.WatchedSubject
import cz.city.honest.repository.DatabaseOperationProvider
import cz.city.honest.repository.Repository
import cz.city.honest.repository.suggestion.SuggestionRepository
import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.core.Observable

class SubjectRepository(
    databaseOperationProvider: DatabaseOperationProvider,
    private val suggestionRepository: SuggestionRepository,
    private val objectMapper: ObjectMapper
) : Repository<WatchedSubject>(databaseOperationProvider) {


    fun get(filter: Filter): Flowable<WatchedSubject> =
        findWatchedSubject(filter)
            .flatMap { toEntities(it) { toWatchedSubject(it) } }

    override fun insert(entity: WatchedSubject): Observable<Long> = Observable.just(
        databaseOperationProvider.writableDatabase.insertWithOnConflict(
            TABLE_NAME,
            null,
            getContentValues(entity),
            SQLiteDatabase.CONFLICT_REPLACE
        )
    )
        .flatMap { Observable.fromIterable(entity.suggestions) }
        .flatMap { suggestionRepository.insert(it) }

    private fun findWatchedSubject(filter: Filter): Flowable<Cursor> =
        Flowable.just(filter)
            .map { it.subjectFilter.honestyStatusVisibilityMap.filter { it.value } }
            .map { it.keys.map { it.name } }
            .map {
                databaseOperationProvider.readableDatabase.rawQuery(
                    "Select id, class, data, honesty_status, watched_to from watched_subject where watched_to = 'null' and honesty_status in( ${
                        mapToQueryParamSymbols(
                            it
                        )
                    }) ",
                    it.toTypedArray()
                )
            }

    private fun toWatchedSubject(cursor: Cursor): Flowable<WatchedSubject> = Flowable.just(
        objectMapper.readValue(cursor.getString(2), getClassForName(cursor.getString(1)))
    )

    private fun getContentValues(entity: WatchedSubject): ContentValues = ContentValues().apply {
        put("id", entity.id)
        put("class", entity.javaClass.name)
        put("data", objectMapper.writeValueAsString(entity))
        put("honesty_status", entity.honestyStatus.name)
        put("watched_to", entity.watchedTo.toString())
    }

    companion object {
        const val TABLE_NAME: String = "watched_subject"
    }

}