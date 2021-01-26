package cz.city.honest.application.model.gateway

import com.jakewharton.retrofit2.adapter.reactor.ReactorCallAdapterFactory
import cz.city.honest.application.model.gateway.server.*
import cz.city.honest.application.model.property.ConnectionProperties
import dagger.Module
import dagger.Provides
import dagger.multibindings.ClassKey
import dagger.multibindings.IntoMap
import retrofit2.Retrofit
import javax.inject.Singleton


@Module
class GatewayModule (){

    //TODO relocate this
    @Provides
    @Singleton
    fun getRetrofit(connectionProperties: ConnectionProperties): Retrofit = Retrofit.Builder()
        .addCallAdapterFactory(ReactorCallAdapterFactory.create())
        .baseUrl(connectionProperties.baseUrl)
        .build()


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
