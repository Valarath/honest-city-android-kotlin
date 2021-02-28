package cz.city.honest.application.model.repository.subject

import android.content.ContentValues
import android.database.Cursor
import cz.city.honest.application.model.dto.Suggestion
import cz.city.honest.application.model.repository.DatabaseOperationProvider
import cz.city.honest.application.model.repository.Repository
import cz.city.honest.application.model.repository.subject.exchange.ExchangeRateRepository
import cz.city.honest.application.model.repository.suggestion.SuggestionRepository
import cz.city.honest.application.model.repository.suggestion.exchange.ExchangeRateSuggestionRepository
import cz.city.honest.mobile.model.dto.*
import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.core.Observable
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.time.LocalDate
import java.util.*

abstract class SubjectRepository<WATCHED_SUBJECT:WatchedSubject>(
    databaseOperationProvider: DatabaseOperationProvider,
    protected val suggestionRepositories:Map<Class<Suggestion>,SuggestionRepository<Suggestion>>,
    protected val exchangeRateRepository: ExchangeRateRepository
) : Repository<WATCHED_SUBJECT>(databaseOperationProvider){


    override fun insert(entity: WATCHED_SUBJECT): Observable<Long> = Observable.just(
        databaseOperationProvider.writableDatabase.insert(
            TABLE_NAME,
            null,
            getContentValues(entity)
        )
    )
        .map { insertSuggestions(entity.suggestions).run { it } }

    private fun insertSuggestions(suggestions:List<Suggestion>)=suggestions.forEach { insertSuggestion(it) }

    private fun insertSuggestion(suggestion: Suggestion) = suggestionRepositories.get(suggestion.javaClass)?.insert(suggestion)

    override fun update(entity: WATCHED_SUBJECT): Observable<Int> = Observable.just(
        databaseOperationProvider.writableDatabase.update(
            TABLE_NAME,
            getContentValues(entity),
            "where id = ?",
            arrayOf(entity.id.toString())
        )
    )
        .map { updateSuggestions(entity.suggestions).run { it } }

    private fun updateSuggestions(suggestions:List<Suggestion>)=suggestions.forEach { updateSuggestion(it) }

    private fun updateSuggestion(suggestion: Suggestion) = suggestionRepositories.get(suggestion.javaClass)?.update(suggestion)

    override fun delete(entity: WATCHED_SUBJECT): Observable<Int> = Observable.just(
        databaseOperationProvider.writableDatabase.delete(
            TABLE_NAME,
            "where id = ?",
            arrayOf(entity.id.toString())
        )
    ) .map { deleteSuggestions(entity.suggestions).run { it } }

    private fun deleteSuggestions(suggestions:List<Suggestion>)=suggestions.forEach { deleteSuggestion(it) }

    private fun deleteSuggestion(suggestion: Suggestion) = suggestionRepositories.get(suggestion.javaClass)?.delete(suggestion)

    private fun getContentValues(entity: WATCHED_SUBJECT): ContentValues = ContentValues().apply {
        put("id", entity.id)
        put("honesty_status", entity.honestyStatus.name)
        put("watched_to", entity.watchedTo.toString())
    }



    protected fun <SUGGESTION_REPOSITORY> getSuggestionRepository(
        repositoryType: Class<SUGGESTION_REPOSITORY>
    ): SUGGESTION_REPOSITORY = suggestionRepositories
        .filter { it.javaClass == repositoryType }
        .map { it as SUGGESTION_REPOSITORY }
        .first()


    companion object {
        const val TABLE_NAME: String = "watched_subject"
    }

}