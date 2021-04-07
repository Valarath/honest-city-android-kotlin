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
import cz.city.honest.application.model.repository.user.UserSuggestionRepository
import cz.city.honest.application.model.repository.vote.VoteRepository
import cz.city.honest.application.model.repository.vote.subject.exchnge.ExchangePointDeleteVoteRepository
import cz.city.honest.application.model.repository.vote.subject.exchnge.ExchangePointRateChangeRepository
import cz.city.honest.application.model.repository.vote.subject.exchnge.NewExchangePointVoteRepository
import cz.city.honest.mobile.model.dto.ExchangePoint
import cz.city.honest.mobile.model.dto.Vote
import cz.city.honest.mobile.model.dto.WatchedSubject
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.multibindings.ClassKey
import dagger.multibindings.IntoMap
import dagger.multibindings.StringKey
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
    @IntoMap
    @StringKey("ExchangePoint")
    fun getExchangePointRepository(
        databaseOperationProvider: DatabaseOperationProvider,
        suggestionRepositories: Map<String, @JvmSuppressWildcards SuggestionRepository<out Suggestion>>,
        exchangeRateRepository: ExchangeRateRepository
    ): SubjectRepository<out WatchedSubject> =
        ExchangePointRepository(
            databaseOperationProvider,
            suggestionRepositories,
            exchangeRateRepository
        )

    @Provides
    @Singleton
    @IntoMap
    @StringKey("ClosedExchangePointSuggestion")
    fun getClosedExchangePointSuggestionRepository(databaseOperationProvider: DatabaseOperationProvider)
            : SuggestionRepository<out Suggestion> =
        ClosedExchangePointSuggestionRepository(databaseOperationProvider)

    @Provides
    @Singleton
    @IntoMap
    @StringKey("ExchangeRateSuggestion")
    fun getExchangeRateSuggestionRepository(
        databaseOperationProvider: DatabaseOperationProvider,
        exchangeRateRepository: ExchangeRateRepository
    ): SuggestionRepository<out Suggestion> =
        ExchangeRateSuggestionRepository(databaseOperationProvider, exchangeRateRepository)

    @Provides
    @Singleton
    @IntoMap
    @StringKey("NewExchangePointSuggestion")
    fun getNewExchangePointSuggestionRepository(databaseOperationProvider: DatabaseOperationProvider)
            : SuggestionRepository<out Suggestion> =
        NewExchangePointSuggestionRepository(databaseOperationProvider)

    @Provides
    @Singleton
    fun getExchangeRateRepository(databaseOperationProvider: DatabaseOperationProvider): ExchangeRateRepository =
        ExchangeRateRepository(databaseOperationProvider)

    @Provides
    @Singleton
    fun getUserSuggestionRepository(
        databaseOperationProvider: DatabaseOperationProvider,
        userRepository: UserRepository,
        suggestionRepositories: Map<String, @JvmSuppressWildcards SuggestionRepository<out Suggestion>>
    ): UserSuggestionRepository =
        UserSuggestionRepository(databaseOperationProvider, userRepository, suggestionRepositories)

    @Provides
    @Singleton
    @IntoMap
    @StringKey("VoteForExchangePointDelete")
    fun getExchangePointDeleteVoteRepository(
        operationProvider: DatabaseOperationProvider,
        suggestionRepositories: Map<String, @JvmSuppressWildcards SuggestionRepository<out Suggestion>>
    ): VoteRepository<out Vote, out Suggestion> =
        ExchangePointDeleteVoteRepository(operationProvider, suggestionRepositories)

    @Provides
    @Singleton
    @IntoMap
    @StringKey("VoteForExchangePointRateChange")
    fun getExchangePointRateChangeRepository(
        operationProvider: DatabaseOperationProvider,
        suggestionRepositories: Map<String, @JvmSuppressWildcards SuggestionRepository<out Suggestion>>
    ): VoteRepository<out Vote, out Suggestion> =
        ExchangePointRateChangeRepository(operationProvider, suggestionRepositories)

    @Provides
    @Singleton
    @IntoMap
    @StringKey("VoteForNewExchangePoint")
    fun getNewExchangePointVoteRepository(
        operationProvider: DatabaseOperationProvider,
        suggestionRepositories: Map<String, @JvmSuppressWildcards SuggestionRepository<out Suggestion>>
    ): VoteRepository<out Vote, out Suggestion> =
        NewExchangePointVoteRepository(operationProvider, suggestionRepositories)

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