package cz.city.honest.service

import cz.city.honest.service.authority.AuthorityService
import cz.city.honest.service.authorization.AuthorizationService
import cz.city.honest.service.authorization.FacebookLoginDataProvider
import cz.city.honest.service.authorization.LoginDataProvider
import cz.city.honest.service.filter.FilterPersistenceHandler
import cz.city.honest.service.filter.FilterService
import cz.city.honest.service.registration.FacebookLoginHandler
import cz.city.honest.service.registration.LoginHandler
import cz.city.honest.service.settings.CurrencySettingsService
import cz.city.honest.service.subject.PositionProvider
import cz.city.honest.service.subject.SubjectService
import cz.city.honest.service.suggestion.SuggestionService
import cz.city.honest.service.update.PrivateUpdatable
import cz.city.honest.service.update.PublicUpdatable
import cz.city.honest.service.update.UpdateService
import cz.city.honest.service.user.UserSuggestionService
import cz.city.honest.service.vote.VoteService
import cz.city.honest.dto.*
import cz.city.honest.external.*
import cz.city.honest.repository.settings.CurrencySettingsRepository
import cz.city.honest.repository.subject.SubjectRepository
import cz.city.honest.repository.suggestion.SuggestionRepository
import cz.city.honest.repository.user.UserSuggestionRepository
import cz.city.honest.repository.vote.VoteRepository
import cz.city.honest.service.user.UserProvider
import cz.city.honest.service.user.UserService
import dagger.MapKey
import dagger.Module
import dagger.Provides
import dagger.multibindings.IntoMap
import dagger.multibindings.StringKey
import javax.inject.Singleton

@Module
class ServiceModule {

    @Provides
    @Singleton
    fun getAuthorityService(
        authorityRepository: cz.city.honest.repository.authority.AuthorityRepository,
        authorityServerSource: AuthorityServerSource
    ): AuthorityService = AuthorityService(authorityRepository, authorityServerSource)

    @Provides
    @Singleton
    fun getSubjectService(
        subjectRepositories: Map<String, @JvmSuppressWildcards SubjectRepository<out WatchedSubject>>,
        subjectServerSource: SubjectServerSource,
        suggestionService: SuggestionService,
        positionProvider: PositionProvider
    ): SubjectService =
        SubjectService(
            subjectRepositories,
            subjectServerSource,
            suggestionService,
            positionProvider
        )

    @Provides
    @Singleton
    fun getSuggestionService(
        suggestionRepositories: Map<String, @JvmSuppressWildcards SuggestionRepository<out Suggestion>>,
        userSuggestionService: UserSuggestionService,
        voteService: VoteService
    ): SuggestionService =
        SuggestionService(
            suggestionRepositories,
            userSuggestionService,
            voteService
        )

    @Provides
    @Singleton
    fun getFilterService(
        filterPersistenceHandler: FilterPersistenceHandler
    ): FilterService =
        FilterService(filterPersistenceHandler)

    @Provides
    @Singleton
    fun getUserSuggestionService(
        userSuggestionRepository: UserSuggestionRepository,
        userProvider: UserProvider,
        voteService: VoteService,
        suggestionServerSource: SuggestionServerSource
    ): UserSuggestionService =
        UserSuggestionService(suggestionServerSource, userSuggestionRepository, voteService, userProvider)

    @Provides
    @Singleton
    fun getVoteService(
        voteServerSource: VoteServerSource,
        voteRepositories: Map<String, @JvmSuppressWildcards VoteRepository<out Vote, out Suggestion>>,
        userProvider: UserProvider
    ): VoteService = VoteService(voteServerSource, voteRepositories, userProvider)

    @Provides
    @Singleton
    fun getAuthorizationService(
        authorizationServerSource: AuthorizationServerSource,
        userService: UserService,
        loginHandlers: Map<String, @JvmSuppressWildcards LoginHandler<out LoginData>>
    ): AuthorizationService = AuthorizationService(authorizationServerSource, userService,loginHandlers)

    @Provides
    @Singleton
    @IntoMap
    @StringKey("FacebookLoginData")
    fun getFacebookLoginHandler(
        serverSource: AuthorizationServerSource,
        userService: UserService
    ): LoginHandler<out LoginData> = FacebookLoginHandler(serverSource,userService)

    @Provides
    @IntoMap
    @LoginProviderKey(LoginProvider.FACEBOOK)
    fun getFacebookLoginDataProvider(
    ): LoginDataProvider<out LoginData> = FacebookLoginDataProvider()


    @Provides
    @Singleton
    fun getUserService(
        userServerSource: UserServerSource,
        userSuggestionRepository: UserSuggestionRepository,
        userRepository: cz.city.honest.repository.user.UserRepository
    ): UserService =
        UserService(userServerSource, userSuggestionRepository,userRepository)

    @Provides
    @Singleton
    fun getPrivateUpdatableServices(
        voteService: VoteService,
        userService: UserService,
        userSuggestionService: UserSuggestionService
    ): List<PrivateUpdatable> =
        listOf( userService,userSuggestionService,voteService)

    @Provides
    @Singleton
    fun getPublicUpdatableServices(
        authorityService: AuthorityService,
        subjectService: SubjectService,
        settingsService: CurrencySettingsService
    ): List<PublicUpdatable> =
        listOf(authorityService, subjectService,settingsService)

    @Provides
    @Singleton
    fun getUpdateService(
        privateUpdatableServices: @JvmSuppressWildcards List<PrivateUpdatable>,
        publicUpdatableServices: @JvmSuppressWildcards List<PublicUpdatable>,
        authorizationService: AuthorizationService
    ): UpdateService =
        UpdateService(privateUpdatableServices, publicUpdatableServices, authorizationService)

    @Provides
    @Singleton
    fun getCurrencySettingsService(currencySettingsRepository: CurrencySettingsRepository, currencyServerSource: CurrencyServerSource): CurrencySettingsService =
        CurrencySettingsService(currencySettingsRepository,currencyServerSource)

}

@Target(
    AnnotationTarget.FUNCTION,
    AnnotationTarget.PROPERTY_GETTER,
    AnnotationTarget.PROPERTY_SETTER
)
@MapKey
annotation class LoginProviderKey(val value: LoginProvider)