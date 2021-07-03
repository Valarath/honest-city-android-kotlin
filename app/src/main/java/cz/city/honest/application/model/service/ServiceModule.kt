package cz.city.honest.application.model.service

import cz.city.honest.application.model.dto.*
import cz.city.honest.application.model.gateway.server.*
import cz.city.honest.application.model.repository.authority.AuthorityRepository
import cz.city.honest.application.model.repository.settings.CurrencySettingsRepository
import cz.city.honest.application.model.repository.subject.SubjectRepository
import cz.city.honest.application.model.repository.suggestion.SuggestionRepository
import cz.city.honest.application.model.repository.user.UserRepository
import cz.city.honest.application.model.repository.user.UserSuggestionRepository
import cz.city.honest.application.model.repository.vote.VoteRepository
import cz.city.honest.application.model.service.authority.AuthorityService
import cz.city.honest.application.model.service.authorization.AuthorizationService
import cz.city.honest.application.model.service.authorization.FacebookLoginDataProvider
import cz.city.honest.application.model.service.authorization.LoginDataProvider
import cz.city.honest.application.model.service.registration.FacebookLoginHandler
import cz.city.honest.application.model.service.registration.LoginHandler
import cz.city.honest.application.model.service.settings.CurrencySettingsService
import cz.city.honest.application.model.service.subject.PositionProvider
import cz.city.honest.application.model.service.subject.SubjectService
import cz.city.honest.application.model.service.suggestion.SuggestionService
import cz.city.honest.application.model.service.update.PrivateUpdatable
import cz.city.honest.application.model.service.update.PublicUpdatable
import cz.city.honest.application.model.service.update.UpdateService
import cz.city.honest.application.model.service.user.UserSuggestionService
import cz.city.honest.application.model.service.vote.VoteService
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
        authorityRepository: AuthorityRepository,
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
    fun getUserSuggestionService(
        userSuggestionRepository: UserSuggestionRepository,
        userProvider: UserProvider,
        suggestionServerSource: SuggestionServerSource
    ): UserSuggestionService =
        UserSuggestionService(suggestionServerSource, userSuggestionRepository, userProvider)

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
        userRepository: UserRepository
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
        //listOf(authorityService, subjectService,settingsService)
        listOf(subjectService,settingsService)

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