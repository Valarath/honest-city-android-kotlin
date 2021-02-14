package cz.city.honest.application.model.service

import cz.city.honest.application.model.dto.Suggestion
import cz.city.honest.application.model.gateway.server.*
import cz.city.honest.application.model.repository.*
import cz.city.honest.application.model.repository.subject.SubjectRepository
import cz.city.honest.application.model.repository.suggestion.SuggestionRepository
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
        subjectRepository: SubjectRepository,
        subjectServerSource: SubjectServerSource
    ): SubjectService =
        SubjectService(subjectRepository, subjectServerSource)

    @Provides
    @Singleton
    fun getSuggestionService(
        suggestionServerSource: SuggestionServerSource,
        suggestionRepositories:List<SuggestionRepository<out Suggestion>>
    ): SuggestionService = SuggestionService(suggestionServerSource, suggestionRepositories)

    @Provides
    @Singleton
    fun getVoteService(
        voteRepository: VoteRepository,
        voteServerSource: VoteServerSource
    ): VoteService = VoteService(voteRepository, voteServerSource)

    @Provides
    @Singleton
    fun getUserService(
        userRepository: UserRepository,
        userServerSource: UserServerSource
    ): UserService = UserService(userRepository, userServerSource)

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
