package cz.city.honest.application.model.service

import cz.city.honest.application.model.repository.SubjectRepository
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class ServiceModule {

    @Provides
    @Singleton
    fun getAuthorityService(): AuthorityService = AuthorityService()

    @Provides
    @Singleton
    fun getSubjectService(subjectRepository: SubjectRepository): SubjectService =
        SubjectService(subjectRepository)

    @Provides
    @Singleton
    fun getSuggestionService(): SuggestionService = SuggestionService()

    @Provides
    @Singleton
    fun getVoteService(): VoteService = VoteService()

    @Provides
    @Singleton
    fun getUserService(): UserService = UserService()

    @Provides
    @Singleton
    fun getSystemService(updateService: UpdateService): SystemService = SystemService(updateService)

    @Provides
    @Singleton
    fun getUpdateService(): UpdateService = UpdateService()
}
