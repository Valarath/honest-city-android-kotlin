package cz.city.honest.application.model.service

import cz.city.honest.application.model.dto.Suggestion
import cz.city.honest.application.model.gateway.server.*
import cz.city.honest.application.model.repository.authority.AuthorityRepository
import cz.city.honest.application.model.repository.subject.SubjectRepository
import cz.city.honest.application.model.repository.suggestion.SuggestionRepository
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
        subjectRepositories: Map<Class<out WatchedSubject>,SubjectRepository<WatchedSubject>>,
        subjectServerSource: SubjectServerSource
    ): SubjectService =
        SubjectService(subjectRepositories, subjectServerSource)

    @Provides
    @Singleton
    fun getSuggestionService(
        suggestionServerSource: SuggestionServerSource,
        suggestionRepositories:Map<Class<out Suggestion>,SuggestionRepository<Suggestion>>
    ): SuggestionService = SuggestionService(suggestionServerSource, suggestionRepositories)

    @Provides
    @Singleton
    fun getVoteService(
        voteServerSource: VoteServerSource
    ): VoteService = VoteService( voteServerSource)

    @Provides
    @Singleton
    fun getUserService(
        userServerSource: UserServerSource
    ): UserService = UserService( userServerSource)

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
