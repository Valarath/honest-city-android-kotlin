package cz.city.honest.application.model.repository

import android.content.Context
import cz.city.honest.application.model.dto.ClosedExchangePointSuggestion
import cz.city.honest.application.model.dto.ExchangeRateSuggestion
import cz.city.honest.application.model.dto.NewExchangePointSuggestion
import cz.city.honest.application.model.dto.Suggestion
import cz.city.honest.application.model.repository.authority.AuthorityRepository
import cz.city.honest.application.model.repository.subject.SubjectRepository
import cz.city.honest.application.model.repository.subject.exchange.ExchangePointRepository
import cz.city.honest.application.model.repository.subject.exchange.ExchangeRateRepository
import cz.city.honest.application.model.repository.suggestion.SuggestionRepository
import cz.city.honest.application.model.repository.suggestion.exchange.ClosedExchangePointSuggestionRepository
import cz.city.honest.application.model.repository.suggestion.exchange.ExchangeRateSuggestionRepository
import cz.city.honest.application.model.repository.suggestion.exchange.NewExchangePointSuggestionRepository
import cz.city.honest.application.model.repository.user.UserRepository
import cz.city.honest.application.model.repository.vote.VoteRepository
import cz.city.honest.mobile.model.dto.ExchangePoint
import cz.city.honest.mobile.model.dto.WatchedSubject
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.multibindings.ClassKey
import dagger.multibindings.IntoMap
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
    fun getUserRepository(
        databaseOperationProvider: DatabaseOperationProvider
    ): UserRepository = UserRepository(databaseOperationProvider)

    @Provides
    @Singleton
    fun getVoteRepository(
        databaseOperationProvider: DatabaseOperationProvider,
        suggestionRepositories: Map<Class<out Suggestion>, @JvmSuppressWildcards SuggestionRepository<out Suggestion>>
    ): VoteRepository = VoteRepository(databaseOperationProvider,suggestionRepositories)

    @Provides
    @Singleton
    //@IntoMap
    //@ClassKey(ExchangePoint::class)
    fun getExchangePointRepository(
        databaseOperationProvider: DatabaseOperationProvider,
        suggestionRepositories: Map<Class<out Suggestion>, @JvmSuppressWildcards SuggestionRepository<out Suggestion>>,
        exchangeRateRepository: ExchangeRateRepository
    ): SubjectRepository< ExchangePoint> =
        ExchangePointRepository(
            databaseOperationProvider,
            suggestionRepositories,
            exchangeRateRepository
        )

    @Provides
    @Singleton
    //@IntoMap
    //@ClassKey(ClosedExchangePointSuggestion::class)
    fun getClosedExchangePointSuggestionRepository(databaseOperationProvider: DatabaseOperationProvider)
            : SuggestionRepository<ClosedExchangePointSuggestion> =
        ClosedExchangePointSuggestionRepository(databaseOperationProvider)

   /* @Provides
    @Singleton
    @IntoMap
    @ClassKey(ExchangeRateSuggestion::class)*/
    fun getExchangeRateSuggestionRepository(
        databaseOperationProvider: DatabaseOperationProvider,
        exchangeRateRepository: ExchangeRateRepository
    ): SuggestionRepository<out ExchangeRateSuggestion> =
    //):ExchangeRateSuggestionRepository =
        ExchangeRateSuggestionRepository(databaseOperationProvider, exchangeRateRepository)

    @Provides
    @Singleton
    @IntoMap
    @ClassKey(NewExchangePointSuggestion::class)
    fun getNewExchangePointSuggestionRepository(databaseOperationProvider: DatabaseOperationProvider)
            : SuggestionRepository<NewExchangePointSuggestion> =
        NewExchangePointSuggestionRepository(databaseOperationProvider)

    @Provides
    @Singleton
    fun getExchangeRateRepository(databaseOperationProvider: DatabaseOperationProvider): ExchangeRateRepository =
        ExchangeRateRepository(databaseOperationProvider)

    @Provides
    @Singleton
    fun getSubjectRepositories(newExchangePointRepository:SubjectRepository< ExchangePoint>):Map<Class<out WatchedSubject>,SubjectRepository<out WatchedSubject>> = mapOf(ExchangePoint::class.java to newExchangePointRepository )

    @Provides
    @Singleton
    fun getSuggestionRepositories(closedExchangePointSuggestionRepository: SuggestionRepository<ClosedExchangePointSuggestion>):Map<Class<out Suggestion>,SuggestionRepository<out Suggestion>> = mapOf(Suggestion::class.java to closedExchangePointSuggestionRepository)

}

data class DatabaseConfiguration(
    val context: Context,
    val name: String,
    val version: Int
)

data class DatabaseProperties(
    val name: String,
    val version: Int
)