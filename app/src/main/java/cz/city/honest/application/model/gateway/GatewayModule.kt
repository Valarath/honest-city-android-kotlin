package cz.city.honest.application.model.gateway

import com.google.gson.GsonBuilder
import com.jakewharton.retrofit2.adapter.reactor.ReactorCallAdapterFactory
import cz.city.honest.application.model.dto.LoginData
import cz.city.honest.application.model.gateway.server.*
import cz.city.honest.application.model.property.ConnectionProperties
import dagger.Module
import dagger.Provides
import dagger.multibindings.ClassKey
import dagger.multibindings.IntoMap
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava3.RxJava3CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton


@Module
class GatewayModule (){

    //TODO relocate this
    @Provides
    @Singleton
    fun getRetrofit(connectionProperties: ConnectionProperties): Retrofit = Retrofit.Builder()
        .addCallAdapterFactory(ReactorCallAdapterFactory.create())
        .baseUrl(connectionProperties.baseUrl)
        .addConverterFactory(getConverterFactory())
        .client(getHttpClient())
        .addCallAdapterFactory(RxJava3CallAdapterFactory.create())
        .build()

    private fun getLoggingInterceptor() = HttpLoggingInterceptor()
        .apply { this.level = HttpLoggingInterceptor.Level.BODY }

    private fun getHttpClient(interceptor: Interceptor = getLoggingInterceptor()) =
        OkHttpClient.Builder()
            .addInterceptor(interceptor)
            .build()

    private fun getConverterFactory() = GsonBuilder()
        .registerTypeAdapter(LoginData::class.java,LoginDataSerializer())
        .run { this.create() }
        .run { GsonConverterFactory.create(this) }

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
