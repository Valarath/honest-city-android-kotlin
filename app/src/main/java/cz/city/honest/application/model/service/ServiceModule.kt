package cz.city.honest.application.model.service

import cz.city.honest.application.model.dto.Suggestion
import cz.city.honest.application.model.gateway.server.*
import cz.city.honest.application.model.repository.authority.AuthorityRepository
import cz.city.honest.application.model.repository.settings.CurrencySettingsRepository
import cz.city.honest.application.model.repository.subject.SubjectRepository
import cz.city.honest.application.model.repository.suggestion.SuggestionRepository
import cz.city.honest.application.model.repository.user.UserSuggestionRepository
import cz.city.honest.application.model.repository.vote.VoteRepository
import cz.city.honest.application.model.service.settings.CurrencySettingsService
import cz.city.honest.application.model.service.vote.VoteService
import cz.city.honest.mobile.model.dto.Vote
import cz.city.honest.mobile.model.dto.WatchedSubject
import dagger.Module
import dagger.Provides
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
        positionProvider: PositionProvider
    ): SubjectService =
        SubjectService(subjectRepositories, subjectServerSource, positionProvider)

    @Provides
    @Singleton
    fun getSuggestionService(
        suggestionRepositories: Map<String, @JvmSuppressWildcards SuggestionRepository<out Suggestion>>,
        userSuggestionService: UserSuggestionService,
        voteService: VoteService
    ): SuggestionService = SuggestionService(suggestionRepositories,userSuggestionService,voteService)

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
    ): VoteService = VoteService(voteServerSource, voteRepositories,userProvider)

    @Provides
    @Singleton
    fun getUserService(
        userServerSource: UserServerSource,
        userProvider: UserProvider,
        userSuggestionRepository: UserSuggestionRepository
    ): UserService = UserService(userServerSource, userProvider, userSuggestionRepository)

    @Provides
    @Singleton
    fun getUpdatableServices(
        authorityService: AuthorityService,
        subjectService: SubjectService,
        userService: UserService,
        settingsService: CurrencySettingsService
    ): List<Updatable> = listOf(authorityService, subjectService, userService,settingsService)

    @Provides
    @Singleton
    fun getUpdateService(updatableServices: @JvmSuppressWildcards List<Updatable>): UpdateService =
        UpdateService(updatableServices)

    @Provides
    @Singleton
    fun getCurrencySettingsService(currencySettingsRepository: CurrencySettingsRepository): CurrencySettingsService =
        CurrencySettingsService(currencySettingsRepository)
}
