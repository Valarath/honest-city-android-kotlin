package cz.city.honest.external

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.databind.module.SimpleModule
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.KotlinModule
import com.jakewharton.retrofit2.adapter.reactor.ReactorCallAdapterFactory
import cz.city.honest.dto.LoginData
import cz.city.honest.dto.Suggestion
import cz.city.honest.dto.Vote
import cz.city.honest.dto.WatchedSubject
import cz.city.honest.property.ConnectionProperties
import cz.city.honest.service.gateway.external.*
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
class ServerSourceModule() {

    //TODO relocate this
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

    private fun getObjectMapper() = getBaseObjectMapper()
        .also { it.registerModule(getModule(it)) }

    private fun getModule(objectMapper: ObjectMapper) = SimpleModule()
        .apply { setDeserializers(this, objectMapper) }

    private fun setDeserializers(module: SimpleModule, objectMapper: ObjectMapper) =
        module.apply {
            this.addDeserializer(
                LoginData::class.java,
                LoginDataSerializer(
                    objectMapper
                )
            )
            this.addDeserializer(
                WatchedSubject::class.java,
                WatchedSubjectSerializer(
                    objectMapper
                )
            )
            this.addDeserializer(
                Suggestion::class.java,
                SuggestionSerializer(
                    objectMapper
                )
            )
            this.addDeserializer(
                Vote::class.java,
                VoteSerializer(
                    objectMapper
                )
            )
        }

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

    private fun <T> getServerSource(retrofit: Retrofit, gateway: Class<T>) =
        retrofit.create(gateway)

    @Provides
    @Singleton
    fun getAuthorityServerSourceService(authorityServerSource: AuthorityServerSource): ExternalAuthorityGateway =
        AuthorityServerSourceService(authorityServerSource)

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

    companion object {
        fun getBaseObjectMapper() = ObjectMapper()
            .also { it.registerModule(KotlinModule()) }
            .also { it.registerModule(JavaTimeModule()) }
            .also { it.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS) }
            .also { it.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false) }
    }
}

abstract class BaseGatewayModule {
    protected fun <T> getGateway(retrofit: Retrofit, gateway: Class<T>): T =
        retrofit.create(gateway)
}

@Module
class AuthorityGatewayModule : BaseGatewayModule() {
    @Provides()
    @IntoMap
    @ClassKey(AuthorityServerSource::class)
    @Singleton
    fun getAuthorityGateway(retrofit: Retrofit): AuthorityServerSource =
        getGateway(retrofit, AuthorityServerSource::class.java)


}