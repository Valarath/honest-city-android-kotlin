package cz.city.honest.application.model.service

import cz.city.honest.application.model.dto.Suggestion
import cz.city.honest.application.model.gateway.server.*
import cz.city.honest.application.model.repository.authority.AuthorityRepository
import cz.city.honest.application.model.repository.subject.SubjectRepository
import cz.city.honest.application.model.repository.suggestion.SuggestionRepository
import cz.city.honest.application.model.repository.user.UserRepository
import cz.city.honest.application.model.repository.user.UserSuggestionRepository
import cz.city.honest.application.model.repository.vote.VoteRepository
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
        subjectRepositories: Map<String,@JvmSuppressWildcards SubjectRepository<out WatchedSubject>>,
        subjectServerSource: SubjectServerSource,
        positionProvider: PositionProvider
    ): SubjectService =
        SubjectService(subjectRepositories, subjectServerSource, positionProvider)

    @Provides
    @Singleton
    fun getSuggestionService(
        userSuggestionRepository: UserSuggestionRepository,
        userService: UserService,
        suggestionServerSource: SuggestionServerSource
    ): SuggestionService = SuggestionService(suggestionServerSource, userSuggestionRepository, userService)

    @Provides
    @Singleton
    fun getVoteService(
        voteServerSource: VoteServerSource,
        voteRepositories: Map<String, @JvmSuppressWildcards VoteRepository<out Vote, out Suggestion>>
    ): VoteService = VoteService( voteServerSource, voteRepositories)

    @Provides
    @Singleton
    fun getUserService(
        userServerSource: UserServerSource,
        userProvider: UserProvider,
        userSuggestionRepository: UserSuggestionRepository
    ): UserService = UserService( userServerSource,userProvider, userSuggestionRepository)

    @Provides
    @Singleton
    fun getUpdatableServices(
        authorityService: AuthorityService,
        subjectService: SubjectService,
        userService: UserService
    ): List<Updatable> = listOf(authorityService, subjectService, userService)

    @Provides
    @Singleton
    fun getUpdateService(updatableServices: @JvmSuppressWildcards List<Updatable>): UpdateService =
        UpdateService(updatableServices)
}
