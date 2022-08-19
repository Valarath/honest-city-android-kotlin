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
        .apply { setDeserializers(this,objectMapper) }

    private fun setDeserializers(module: SimpleModule,objectMapper: ObjectMapper) =
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
        getGateway(retrofit, AuthorityServerSource::class.java)


    @Provides
    @Singleton
    fun getSubjectGateway(retrofit: Retrofit): SubjectServerSource =
        getGateway(retrofit, SubjectServerSource::class.java)

    @Provides
    @Singleton
    fun getSuggestionGateway(retrofit: Retrofit): SuggestionServerSource =
        getGateway(retrofit, SuggestionServerSource::class.java)

    @Provides
    @Singleton
    fun getUserGateway(retrofit: Retrofit): UserServerSource =
        getGateway(retrofit, UserServerSource::class.java)

    @Provides
    @Singleton
    fun getVoteGateway(retrofit: Retrofit): VoteServerSource =
        getGateway(retrofit, VoteServerSource::class.java)

    @Provides
    @Singleton
    fun getCurrencyServerSource(retrofit: Retrofit): CurrencyServerSource =
        getGateway(retrofit, CurrencyServerSource::class.java)

    @Provides
    @Singleton
    fun getAuthorizationServerSource(retrofit: Retrofit): AuthorizationServerSource =
        getGateway(retrofit, AuthorizationServerSource::class.java)

    private fun <T> getGateway(retrofit: Retrofit, gateway: Class<T>) = retrofit.create(gateway)

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