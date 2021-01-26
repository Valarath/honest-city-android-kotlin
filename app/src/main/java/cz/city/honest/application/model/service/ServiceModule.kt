package cz.city.honest.application.model.service

import cz.city.honest.application.model.gateway.server.*
import cz.city.honest.application.model.repository.*
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
        suggestionRepository: SuggestionRepository
    ): SuggestionService = SuggestionService(suggestionServerSource, suggestionRepository)

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
        suggestionService: SuggestionService,
        subjectService: SubjectService,
        userService: UserService
    ): List<Updatable> = listOf(authorityService, subjectService, suggestionService, userService)

    @Provides
    @Singleton
    fun getUpdateService(updatableServices: @JvmSuppressWildcards List<Updatable>): UpdateService =
        UpdateService(updatableServices)
}
