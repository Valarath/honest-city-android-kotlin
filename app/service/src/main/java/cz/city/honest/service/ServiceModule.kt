package cz.city.honest.service

import cz.city.honest.dto.LoginData
import cz.city.honest.service.analyze.AnalyticService
import cz.city.honest.service.authority.AuthorityService
import cz.city.honest.service.authorization.AuthorizationService
import cz.city.honest.service.filter.FilterService
import cz.city.honest.service.gateway.external.*
import cz.city.honest.service.gateway.internal.*
import cz.city.honest.service.position.PositionService
import cz.city.honest.service.settings.CurrencySettingsService
import cz.city.honest.service.settings.SubjectSettingsService
import cz.city.honest.service.subject.SubjectService
import cz.city.honest.service.suggestion.SuggestionService
import cz.city.honest.service.update.PrivateUpdatable
import cz.city.honest.service.update.PublicUpdatable
import cz.city.honest.service.update.UpdateService
import cz.city.honest.service.user.UserProvider
import cz.city.honest.service.user.UserService
import cz.city.honest.service.user.UserSuggestionService
import cz.city.honest.service.vote.VoteService
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class ServiceModule {
    companion object {
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
            positionService: PositionService
        ): SubjectService =
            SubjectService(
                internalSubjectGateway,
                externalSubjectGateway,
                suggestionService,
                positionService
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
        fun getPositionService(
            internalPositionGateway: InternalPositionGateway
        ): PositionService =
            PositionService(internalPositionGateway)

        @Provides
        @Singleton
        fun getExchangeNameService(
            internalSubjectSettingsGateway: InternalSubjectSettingsGateway,
            externalSubjectSettingsGateway: ExternalSubjectSettingsGateway
        ): SubjectSettingsService =
            SubjectSettingsService(
                internalSubjectSettingsGateway,
                externalSubjectSettingsGateway
            )

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
            externalTokenValidationGateway: ExternalTokenValidationGateway,
            externalAuthorizationGateway: ExternalAuthorizationGateway,
            userService: UserService,
            internalAuthorizationGateways: Map<String, @JvmSuppressWildcards InternalAuthorizationGateway<out LoginData>>
        ): AuthorizationService =
            AuthorizationService(
                externalTokenValidationGateway,
                externalAuthorizationGateway,
                userService,
                internalAuthorizationGateways
            )

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
        fun getAnalyticService(
            internalImageRateAnalyticGateway: InternalImageRateAnalyticGateway,
            internalImageNameAnalyticGateway: InternalImageNameAnalyticGateway
        ): AnalyticService =
            AnalyticService(internalImageRateAnalyticGateway, internalImageNameAnalyticGateway)

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
            settingsService: CurrencySettingsService,
            subjectSettingsService: SubjectSettingsService
        ): List<PublicUpdatable> =
            listOf(authorityService, subjectService, settingsService, subjectSettingsService)

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
}