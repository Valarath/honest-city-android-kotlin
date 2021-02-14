package cz.city.honest.application.model.repository

import ClosedExchangePointSuggestionRepository
import android.content.Context
import cz.city.honest.application.model.dto.Suggestion
import cz.city.honest.application.model.repository.subject.SubjectRepository
import cz.city.honest.application.model.repository.suggestion.ExchangeRateSuggestionRepository
import cz.city.honest.application.model.repository.suggestion.NewExchangePointSuggestionRepository
import cz.city.honest.application.model.repository.suggestion.SuggestionRepository
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class RepositoryModule() {

    @Provides
    @Singleton
    fun getDatabaseOperationProvider(databaseConfiguration: DatabaseConfiguration): DatabaseOperationProvider =
        DatabaseOperationProvider(databaseConfiguration)

    //TODO to properties
    @Provides
    @Singleton
    fun getDatabaseConfiguration(context: Context): DatabaseConfiguration =
        DatabaseConfiguration(context, "honest_city", 1)

    @Provides
    @Singleton
    fun getAuthorityRepository(
        databaseOperationProvider: DatabaseOperationProvider,
        exchangeRateRepository: ExchangeRateRepository
    ): AuthorityRepository = AuthorityRepository(databaseOperationProvider, exchangeRateRepository)

    @Provides
    @Singleton
    fun getSubjectRepository(
        databaseOperationProvider: DatabaseOperationProvider,
        suggestionRepositories:List<SuggestionRepository<out Suggestion>>,
        exchangeRateRepository: ExchangeRateRepository
    ): SubjectRepository =
        SubjectRepository(databaseOperationProvider, suggestionRepositories, exchangeRateRepository)

    @Provides
    @Singleton
    fun getClosedExchangePointSuggestionRepository(databaseOperationProvider: DatabaseOperationProvider)
            : ClosedExchangePointSuggestionRepository =
        ClosedExchangePointSuggestionRepository(databaseOperationProvider)

    @Provides
    @Singleton
    fun getExchangeRateSuggestionRepository(
        databaseOperationProvider: DatabaseOperationProvider,
        exchangeRateRepository: ExchangeRateRepository
    ): ExchangeRateSuggestionRepository =
        ExchangeRateSuggestionRepository(databaseOperationProvider,exchangeRateRepository)

    @Provides
    @Singleton
    fun getNewExchangePointSuggestionRepository(databaseOperationProvider: DatabaseOperationProvider)
            : NewExchangePointSuggestionRepository =
        NewExchangePointSuggestionRepository(databaseOperationProvider)


    @Provides
    @Singleton
    fun getSuggestionRepositories(
        newExchangePointSuggestionRepository: NewExchangePointSuggestionRepository,
        closedExchangePointSuggestionRepository: ClosedExchangePointSuggestionRepository,
        exchangeRateSuggestionRepository: ExchangeRateSuggestionRepository
    ) = listOf(
        newExchangePointSuggestionRepository,
        closedExchangePointSuggestionRepository,
        exchangeRateSuggestionRepository
    )

    @Provides
    @Singleton
    fun getUserRepository(databaseOperationProvider: DatabaseOperationProvider): UserRepository =
        UserRepository(databaseOperationProvider)

    @Provides
    @Singleton
    fun getVoteRepository(databaseOperationProvider: DatabaseOperationProvider): VoteRepository =
        VoteRepository(databaseOperationProvider)

    @Provides
    @Singleton
    fun getExchangeRateRepository(databaseOperationProvider: DatabaseOperationProvider): ExchangeRateRepository =
        ExchangeRateRepository(databaseOperationProvider)

}

data class DatabaseConfiguration(
    val context: Context,
    val name: String,
    val version: Int
)