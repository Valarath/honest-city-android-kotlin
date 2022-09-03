package cz.city.honest.repository

import android.content.Context
import cz.city.honest.dto.LoginData
import cz.city.honest.dto.Suggestion
import cz.city.honest.dto.Vote
import cz.city.honest.dto.WatchedSubject
import cz.city.honest.repository.authority.AuthorityRepository
import cz.city.honest.repository.authority.AuthorityService
import cz.city.honest.repository.autorization.FacebookLoginDataRepository
import cz.city.honest.repository.autorization.LoginDataRepository
import cz.city.honest.repository.settings.CurrencySettingsRepository
import cz.city.honest.repository.settings.CurrencySettingsService
import cz.city.honest.repository.subject.SubjectRepository
import cz.city.honest.repository.subject.SubjectService
import cz.city.honest.repository.subject.exchange.ExchangePointRepository
import cz.city.honest.repository.subject.exchange.ExchangeRateRepository
import cz.city.honest.repository.suggestion.SuggestionRepository
import cz.city.honest.repository.suggestion.SuggestionService
import cz.city.honest.repository.suggestion.exchange.ClosedExchangePointSuggestionRepository
import cz.city.honest.repository.suggestion.exchange.ExchangeRateSuggestionRepository
import cz.city.honest.repository.suggestion.exchange.NewExchangePointSuggestionRepository
import cz.city.honest.repository.user.UserRepository
import cz.city.honest.repository.user.UserService
import cz.city.honest.repository.user.UserSuggestionRepository
import cz.city.honest.repository.user.UserSuggestionService
import cz.city.honest.repository.vote.VoteRepository
import cz.city.honest.repository.vote.VoteService
import cz.city.honest.repository.vote.subject.exchnge.ExchangePointDeleteVoteRepository
import cz.city.honest.repository.vote.subject.exchnge.ExchangePointRateChangeVoteRepository
import cz.city.honest.repository.vote.subject.exchnge.NewExchangePointVoteRepository
import cz.city.honest.service.gateway.internal.*
import dagger.Module
import dagger.Provides
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
    fun getCurrencySettingsRepository(
        databaseOperationProvider: DatabaseOperationProvider
    ): CurrencySettingsRepository = CurrencySettingsRepository(databaseOperationProvider)

    @Provides
    @Singleton
    @IntoMap
    @StringKey("FacebookLoginData")
    fun getFacebookLoginDataRepository(
        databaseOperationProvider: DatabaseOperationProvider
    ): LoginDataRepository<out LoginData> = FacebookLoginDataRepository(databaseOperationProvider)

    @Provides
    @Singleton
    fun getUserRepository(
        databaseOperationProvider: DatabaseOperationProvider,
        loginDataRepositories: Map<String, @JvmSuppressWildcards LoginDataRepository<out LoginData>>
    ): UserRepository = UserRepository(databaseOperationProvider, loginDataRepositories)

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
        ExchangePointRateChangeVoteRepository(operationProvider, suggestionRepositories)

    @Provides
    @Singleton
    @IntoMap
    @StringKey("VoteForNewExchangePoint")
    fun getNewExchangePointVoteRepository(
        operationProvider: DatabaseOperationProvider,
        suggestionRepositories: Map<String, @JvmSuppressWildcards SuggestionRepository<out Suggestion>>
    ): VoteRepository<out Vote, out Suggestion> =
        NewExchangePointVoteRepository(operationProvider, suggestionRepositories)

    @Provides
    @Singleton
    fun getAuthorityService(authorityRepository: AuthorityRepository): InternalAuthorityGateway =
        AuthorityService(authorityRepository)

    @Provides
    @Singleton
    fun getCurrencySettingsService(currencySettingsRepository: CurrencySettingsRepository): InternalCurrencySettingsGateway =
        CurrencySettingsService(currencySettingsRepository)

    @Provides
    @Singleton
    fun getSubjectService(subjectRepositories: Map<String, @JvmSuppressWildcards SubjectRepository<out WatchedSubject>>): InternalSubjectGateway =
        SubjectService(subjectRepositories)

    @Provides
    @Singleton
    fun getSuggestionService(suggestionRepositories: Map<String, @JvmSuppressWildcards SuggestionRepository<out Suggestion>>): InternalSuggestionGateway =
        SuggestionService(suggestionRepositories)

    @Provides
    @Singleton
    fun getUserService(userRepository: UserRepository): InternalUserGateway =
        UserService(userRepository)

    @Provides
    @Singleton
    fun getUserSuggestionService(userSuggestionRepository: UserSuggestionRepository): InternalUserSuggestionGateway =
        UserSuggestionService(userSuggestionRepository)

    @Provides
    @Singleton
    fun getVoteService(voteRepositories: Map<String, @JvmSuppressWildcards VoteRepository<out Vote, out Suggestion>>): InternalVoteGateway =
        VoteService(voteRepositories)

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