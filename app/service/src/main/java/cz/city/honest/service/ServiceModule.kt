package cz.city.honest.service

import cz.city.honest.dto.LoginData
import cz.city.honest.dto.LoginProvider
import cz.city.honest.external.AuthorizationServerSource
import cz.city.honest.service.authority.AuthorityService
import cz.city.honest.service.authorization.AuthorizationService
import cz.city.honest.service.authorization.FacebookLoginDataProvider
import cz.city.honest.service.authorization.LoginDataProvider
import cz.city.honest.service.filter.FilterService
import cz.city.honest.service.gateway.external.*
import cz.city.honest.service.gateway.internal.*
import cz.city.honest.service.registration.FacebookLoginHandler
import cz.city.honest.service.registration.LoginHandler
import cz.city.honest.service.settings.CurrencySettingsService
import cz.city.honest.service.subject.PositionProvider
import cz.city.honest.service.subject.SubjectService
import cz.city.honest.service.suggestion.SuggestionService
import cz.city.honest.service.update.PrivateUpdatable
import cz.city.honest.service.update.PublicUpdatable
import cz.city.honest.service.update.UpdateService
import cz.city.honest.service.user.UserProvider
import cz.city.honest.service.user.UserService
import cz.city.honest.service.user.UserSuggestionService
import cz.city.honest.service.vote.VoteService
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
        internalAuthorityGateway: InternalAuthorityGateway,
        externalAuthorityGateway: ExternalAuthorityGateway
    ): AuthorityService = AuthorityService(internalAuthorityGateway, externalAuthorityGateway)

    @Provides
    @Singleton
    fun getSubjectService(
        internalSubjectGateway: InternalSubjectGateway,
        externalSubjectGateway: ExternalSubjectGateway,
        suggestionService: SuggestionService,
        positionProvider: PositionProvider
    ): SubjectService =
        SubjectService(
            internalSubjectGateway,
            externalSubjectGateway,
            suggestionService,
            positionProvider
        )

    @Provides
    @Singleton
    fun getSuggestionService(
        internalSuggestionGateway: InternalSuggestionGateway,
        userSuggestionService: UserSuggestionService,
        voteService: VoteService
    ): SuggestionService =
        SuggestionService(
            internalSuggestionGateway,
            userSuggestionService,
            voteService
        )

    @Provides
    @Singleton
    fun getFilterService(
        filterPersistenceHandler: InternalFilterGateway
    ): FilterService =
        FilterService(filterPersistenceHandler)

    @Provides
    @Singleton
    fun getUserSuggestionService(
        externalSuggestionGateway: ExternalSuggestionGateway,
        internalUserSuggestionGateway: InternalUserSuggestionGateway,
        userProvider: UserProvider,
        voteService: VoteService,
    ): UserSuggestionService =
        UserSuggestionService(
            externalSuggestionGateway,
            internalUserSuggestionGateway,
            voteService,
            userProvider
        )

    @Provides
    @Singleton
    fun getVoteService(
        internalVoteGateway: InternalVoteGateway,
        externalVoteGateway: ExternalVoteGateway,
        userProvider: UserProvider
    ): VoteService = VoteService(internalVoteGateway, externalVoteGateway, userProvider)

    @Provides
    @Singleton
    fun getAuthorizationService(
        externalAuthorizationGateway: ExternalAuthorizationGateway,
        userService: UserService,
        loginHandlers: Map<String, @JvmSuppressWildcards LoginHandler<out LoginData>>
    ): AuthorizationService =
        AuthorizationService(externalAuthorizationGateway, userService, loginHandlers)

    @Provides
    @Singleton
    @IntoMap
    @StringKey("FacebookLoginData")
    fun getFacebookLoginHandler(
        serverSource: AuthorizationServerSource,
        userService: UserService
    ): LoginHandler<out LoginData> = FacebookLoginHandler(serverSource, userService)

    @Provides
    @IntoMap
    @LoginProviderKey(LoginProvider.FACEBOOK)
    fun getFacebookLoginDataProvider(
    ): LoginDataProvider<out LoginData> = FacebookLoginDataProvider()


    @Provides
    @Singleton
    fun getUserService(
        externalUserGateway: ExternalUserGateway,
        internalUserSuggestionGateway: InternalUserSuggestionGateway,
        internalUserGateway: InternalUserGateway
    ): UserService =
        UserService(externalUserGateway, internalUserSuggestionGateway, internalUserGateway)

    @Provides
    @Singleton
    fun getPrivateUpdatableServices(
        voteService: VoteService,
        userService: UserService,
        userSuggestionService: UserSuggestionService
    ): List<PrivateUpdatable> =
        listOf(userService, userSuggestionService, voteService)

    @Provides
    @Singleton
    fun getPublicUpdatableServices(
        authorityService: AuthorityService,
        subjectService: SubjectService,
        settingsService: CurrencySettingsService
    ): List<PublicUpdatable> =
        listOf(authorityService, subjectService, settingsService)

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
    fun getCurrencySettingsService(
        internalCurrencySettingsGateway: InternalCurrencySettingsGateway,
        externalCurrencyGateway: ExternalCurrencyGateway
    ): CurrencySettingsService =
        CurrencySettingsService(internalCurrencySettingsGateway, externalCurrencyGateway)

}

@Target(
    AnnotationTarget.FUNCTION,
    AnnotationTarget.PROPERTY_GETTER,
    AnnotationTarget.PROPERTY_SETTER
)
@MapKey
annotation class LoginProviderKey(val value: LoginProvider)