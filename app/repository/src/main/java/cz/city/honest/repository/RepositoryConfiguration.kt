package cz.city.honest.repository

import android.content.Context
import cz.city.honest.repository.authority.AuthorityRepository
import cz.city.honest.repository.authority.AuthorityService
import cz.city.honest.repository.autorization.LoginDataRepository
import cz.city.honest.repository.position.PositionRepository
import cz.city.honest.repository.position.PositionService
import cz.city.honest.repository.settings.CurrencySettingsRepository
import cz.city.honest.repository.settings.CurrencySettingsService
import cz.city.honest.repository.settings.SubjectSettingsService
import cz.city.honest.repository.settings.SubjectSettingsRepository
import cz.city.honest.repository.subject.SubjectRepository
import cz.city.honest.repository.subject.SubjectService
import cz.city.honest.repository.suggestion.SuggestionRepository
import cz.city.honest.repository.suggestion.SuggestionService
import cz.city.honest.repository.user.UserRepository
import cz.city.honest.repository.user.UserService
import cz.city.honest.repository.user.UserSuggestionRepository
import cz.city.honest.repository.user.UserSuggestionService
import cz.city.honest.repository.vote.VoteRepository
import cz.city.honest.repository.vote.VoteService
import cz.city.honest.service.gateway.internal.*
import cz.city.honest.service.mapping.ObjectMapperProvider
import cz.city.honest.service.provider.PropertyProvider
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class RepositoryModule() {

    companion object {
        @Provides
        @Singleton
        fun getDatabaseOperationProvider(databaseConfiguration: DatabaseConfiguration): DatabaseOperationProvider =
            DatabaseOperationProvider(databaseConfiguration)

        @Provides
        @Singleton
        fun getDatabaseConfiguration(context: Context, databaseProperties: DatabaseProperties): DatabaseConfiguration =
            DatabaseConfiguration(context, databaseProperties.name, databaseProperties.version)

        @Provides
        @Singleton
        fun getDatabaseProperties(propertyProvider: PropertyProvider): DatabaseProperties =
            propertyProvider.providePropertyOfType(DatabaseProperties::class.java)

        @Provides
        @Singleton
        fun getAuthorityRepository(
            databaseOperationProvider: DatabaseOperationProvider
        ): AuthorityRepository =
            AuthorityRepository(databaseOperationProvider, getObjectMapper())

        @Provides
        @Singleton
        fun getCurrencySettingsRepository(
            databaseOperationProvider: DatabaseOperationProvider
        ): CurrencySettingsRepository = CurrencySettingsRepository(databaseOperationProvider)

        @Provides
        @Singleton
        fun getLoginDataRepository(
            databaseOperationProvider: DatabaseOperationProvider
        ): LoginDataRepository = LoginDataRepository(databaseOperationProvider, getObjectMapper())

        @Provides
        @Singleton
        fun getPositionRepository(
            databaseOperationProvider: DatabaseOperationProvider
        ): PositionRepository = PositionRepository(databaseOperationProvider)

        @Provides
        @Singleton
        fun getUserRepository(
            databaseOperationProvider: DatabaseOperationProvider,
            loginDataRepository: LoginDataRepository
        ): UserRepository = UserRepository(databaseOperationProvider, loginDataRepository)

        @Provides
        @Singleton
        fun getSuggestionRepository(databaseOperationProvider: DatabaseOperationProvider): SuggestionRepository =
            SuggestionRepository(databaseOperationProvider,getObjectMapper())

        @Provides
        @Singleton
        fun getSubjectRepository(databaseOperationProvider: DatabaseOperationProvider, suggestionRepository: SuggestionRepository): SubjectRepository =
            SubjectRepository(databaseOperationProvider,suggestionRepository, getObjectMapper())

        @Provides
        @Singleton
        fun getUserSuggestionRepository(
            databaseOperationProvider: DatabaseOperationProvider,
            userRepository: UserRepository,
            suggestionRepository:SuggestionRepository
        ): UserSuggestionRepository =
            UserSuggestionRepository(
                databaseOperationProvider,
                userRepository,
                suggestionRepository
            )

        @Provides
        @Singleton
        fun getVoteRepository(
            operationProvider: DatabaseOperationProvider,
            suggestionRepository: SuggestionRepository
        ): VoteRepository =
            VoteRepository(operationProvider, suggestionRepository)

        @Provides
        @Singleton
        fun getExchangeNameSettingsRepository(
            operationProvider: DatabaseOperationProvider
        ): SubjectSettingsRepository =
            SubjectSettingsRepository(operationProvider, getObjectMapper())

        @Provides
        @Singleton
        fun getAuthorityService(authorityRepository: AuthorityRepository): InternalAuthorityGateway =
            AuthorityService(authorityRepository)

        @Provides
        @Singleton
        fun getExchangeNameService(subjectSettingsRepository: SubjectSettingsRepository): InternalSubjectSettingsGateway =
            SubjectSettingsService(subjectSettingsRepository)

        @Provides
        @Singleton
        fun getCurrencySettingsService(currencySettingsRepository: CurrencySettingsRepository): InternalCurrencySettingsGateway =
            CurrencySettingsService(currencySettingsRepository)

        @Provides
        @Singleton
        fun getSubjectService(subjectRepository: SubjectRepository): InternalSubjectGateway =
            SubjectService(subjectRepository)

        @Provides
        @Singleton
        fun getSuggestionService(suggestionRepository: SuggestionRepository): InternalSuggestionGateway =
            SuggestionService(suggestionRepository)

        @Provides
        @Singleton
        fun getUserService(userRepository: UserRepository): InternalUserGateway =
            UserService(userRepository)

        @Provides
        @Singleton
        fun getPositionService(positionRepository: PositionRepository): InternalPositionGateway =
            PositionService(positionRepository)

        @Provides
        @Singleton
        fun getUserSuggestionService(userSuggestionRepository: UserSuggestionRepository): InternalUserSuggestionGateway =
            UserSuggestionService(userSuggestionRepository)

        @Provides
        @Singleton
        fun getVoteService(voteRepositories: VoteRepository): InternalVoteGateway =
            VoteService(voteRepositories)

        private fun getObjectMapper() = ObjectMapperProvider.getObjectMapper()
    }
}

data class DatabaseProperties(val name: String, val version: Int){
    constructor():this("",0)
}

data class DatabaseConfiguration(
    val context: Context,
    val name: String,
    val version: Int
)