package cz.city.honest.external

import com.jakewharton.retrofit2.adapter.reactor.ReactorCallAdapterFactory
import cz.city.honest.external.authority.AuthorityServerSource
import cz.city.honest.external.authority.AuthorityServerSourceService
import cz.city.honest.external.autorization.AuthorizationServerSource
import cz.city.honest.external.autorization.AuthorizationServerSourceService
import cz.city.honest.external.settings.CurrencyServerSource
import cz.city.honest.external.settings.CurrencyServerSourceService
import cz.city.honest.external.settings.SubjectSettingsServerSource
import cz.city.honest.external.settings.ExternalSubjectSettingsServerSourceService
import cz.city.honest.external.subject.SubjectServerSource
import cz.city.honest.external.subject.SubjectServerSourceService
import cz.city.honest.external.suggestion.SuggestionServerSource
import cz.city.honest.external.suggestion.SuggestionServerSourceService
import cz.city.honest.external.user.UserServerSource
import cz.city.honest.external.user.UserServerSourceService
import cz.city.honest.external.validation.TokenValidationServerSource
import cz.city.honest.external.validation.TokenValidationServerSourceService
import cz.city.honest.external.vote.VoteServerSource
import cz.city.honest.external.vote.VoteServerSourceService
import cz.city.honest.service.gateway.external.*
import cz.city.honest.service.mapping.ObjectMapperProvider
import cz.city.honest.service.provider.PropertyProvider
import dagger.Module
import dagger.Provides
import dagger.multibindings.ClassKey
import dagger.multibindings.IntoMap
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava3.RxJava3CallAdapterFactory
import retrofit2.converter.jackson.JacksonConverterFactory
import javax.inject.Singleton

@Module
class ExternalSourceModule() {
    companion object {
        @Provides
        @Singleton
        fun getRetrofit(connectionProperties: ConnectionProperties): Retrofit = Retrofit.Builder()
            .addCallAdapterFactory(ReactorCallAdapterFactory.create())
            .baseUrl(connectionProperties.baseUrl)
            .addConverterFactory(JacksonConverterFactory.create(getObjectMapper()))
            .client(getHttpClient())
            .addCallAdapterFactory(RxJava3CallAdapterFactory.create())
            .build()

        private fun getLoggingInterceptor() = HttpLoggingInterceptor()
            .apply { this.level = HttpLoggingInterceptor.Level.BODY }

        private fun getHttpClient(interceptor: Interceptor = getLoggingInterceptor()) =
            OkHttpClient.Builder()
                .addInterceptor(interceptor)
                .build()

        @Provides
        @Singleton
        fun getConnectionProperties(propertyProvider: PropertyProvider): ConnectionProperties =
            propertyProvider.providePropertyOfType(ConnectionProperties::class.java)

        @Provides
        @Singleton
        fun getAuthorityGateway(retrofit: Retrofit): AuthorityServerSource =
            getServerSource(retrofit, AuthorityServerSource::class.java)

        @Provides
        @Singleton
        fun getSubjectGateway(retrofit: Retrofit): SubjectServerSource =
            getServerSource(retrofit, SubjectServerSource::class.java)

        @Provides
        @Singleton
        fun getSuggestionGateway(retrofit: Retrofit): SuggestionServerSource =
            getServerSource(retrofit, SuggestionServerSource::class.java)

        @Provides
        @Singleton
        fun getExchangeNameGateway(retrofit: Retrofit): SubjectSettingsServerSource =
            getServerSource(retrofit, SubjectSettingsServerSource::class.java)

        @Provides
        @Singleton
        fun getUserGateway(retrofit: Retrofit): UserServerSource =
            getServerSource(retrofit, UserServerSource::class.java)

        @Provides
        @Singleton
        fun getVoteGateway(retrofit: Retrofit): VoteServerSource =
            getServerSource(retrofit, VoteServerSource::class.java)

        @Provides
        @Singleton
        fun getCurrencyServerSource(retrofit: Retrofit): CurrencyServerSource =
            getServerSource(retrofit, CurrencyServerSource::class.java)

        @Provides
        @Singleton
        fun getAuthorizationServerSource(retrofit: Retrofit): AuthorizationServerSource =
            getServerSource(retrofit, AuthorizationServerSource::class.java)

        @Provides
        @Singleton
        fun getTokenValidationServerSource(retrofit: Retrofit): TokenValidationServerSource =
            getServerSource(retrofit, TokenValidationServerSource::class.java)

        private fun <T> getServerSource(retrofit: Retrofit, gateway: Class<T>) =
            retrofit.create(gateway)

        @Provides
        @Singleton
        fun getAuthorityServerSourceService(authorityServerSource: AuthorityServerSource): ExternalAuthorityGateway =
            AuthorityServerSourceService(authorityServerSource)

        @Provides
        @Singleton
        fun getExchangeNameServerSourceService(subjectSettingsServerSource: SubjectSettingsServerSource): ExternalSubjectSettingsGateway =
            ExternalSubjectSettingsServerSourceService(subjectSettingsServerSource)

        @Provides
        @Singleton
        fun getAuthorizationServerSourceService(authorityServerSource: AuthorizationServerSource): ExternalAuthorizationGateway =
            AuthorizationServerSourceService(authorityServerSource)

        @Provides
        @Singleton
        fun getCurrencyServerSourceService(currencyServerSource: CurrencyServerSource): ExternalCurrencyGateway =
            CurrencyServerSourceService(currencyServerSource)

        @Provides
        @Singleton
        fun getSubjectServerSourceService(subjectServerSource: SubjectServerSource): ExternalSubjectGateway =
            SubjectServerSourceService(subjectServerSource)

        @Provides
        @Singleton
        fun getSuggestionServerSourceService(suggestionServerSource: SuggestionServerSource): ExternalSuggestionGateway =
            SuggestionServerSourceService(suggestionServerSource)

        @Provides
        @Singleton
        fun getUserServerSourceService(userServerSource: UserServerSource): ExternalUserGateway =
            UserServerSourceService(userServerSource)

        @Provides
        @Singleton
        fun getVoteServerSourceService(voteServerSource: VoteServerSource): ExternalVoteGateway =
            VoteServerSourceService(voteServerSource)

        @Provides
        @Singleton
        fun getTokenValidationServerSourceService(tokenValidationServerSource: TokenValidationServerSource): ExternalTokenValidationGateway =
            TokenValidationServerSourceService(tokenValidationServerSource)

        private fun getObjectMapper() = ObjectMapperProvider.getObjectMapper()
    }
}

data class ConnectionProperties(var baseUrl:String){
    constructor():this("")
}

abstract class BaseGatewayModule {
    companion object {
        @JvmStatic
        protected fun <T> getGateway(retrofit: Retrofit, gateway: Class<T>): T =
            retrofit.create(gateway)
    }
}

@Module
class AuthorityGatewayModule : BaseGatewayModule() {
    companion object {
        @Provides()
        @IntoMap
        @ClassKey(AuthorityServerSource::class)
        @Singleton
        fun getAuthorityGateway(retrofit: Retrofit): AuthorityServerSource =
            getGateway(retrofit, AuthorityServerSource::class.java)
    }

}