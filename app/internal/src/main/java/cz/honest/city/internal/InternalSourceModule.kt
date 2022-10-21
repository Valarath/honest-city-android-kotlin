package cz.honest.city.internal

import android.content.Context
import cz.honest.city.internal.filter.FilterSharedPreferenceRepository
import cz.honest.city.internal.provider.AndroidPositionProvider
import cz.honest.city.internal.provider.AndroidUserProvider
import cz.city.honest.analyzer.ExchangeRateAnalyzer
import cz.city.honest.dto.LoginData
import cz.city.honest.dto.Suggestion
import cz.city.honest.service.gateway.external.ExternalAuthorizationGateway
import cz.city.honest.service.gateway.internal.InternalAuthorizationGateway
import cz.city.honest.service.gateway.internal.InternalFilterGateway
import cz.city.honest.service.subject.PositionProvider
import cz.honest.city.internal.provider.rate.ImageExchangeRateProvider
import cz.honest.city.internal.provider.rate.ImageExchangeRateResultProvider
import cz.city.honest.service.user.UserProvider
import cz.city.honest.service.user.UserService
import cz.honest.city.internal.authentication.FacebookLoginHandler
import dagger.Module
import dagger.Provides
import dagger.multibindings.IntoMap
import dagger.multibindings.StringKey
import javax.inject.Singleton

@Module
class InternalSourceModule {

    @Provides
    @Singleton
    fun getPositionProvider(context: Context): PositionProvider = AndroidPositionProvider(context)


    @Provides
    @Singleton
    fun getFilterPersistenceHandler(context: Context): InternalFilterGateway =
        FilterSharedPreferenceRepository(context)

    @Provides
    @Singleton
    fun getUserProvider(userService: UserService): UserProvider = AndroidUserProvider(userService)

    @Provides
    @Singleton
    fun getImageExchangeRateResultProvider(): ImageExchangeRateResultProvider =
        ImageExchangeRateResultProvider()

    @Provides
    @Singleton
    fun getImageExchangeRateProvider(
        imageExchangeRateResultProvider: ImageExchangeRateResultProvider,
        exchangeRateAnalyzers: List<@JvmSuppressWildcards ExchangeRateAnalyzer>
    ): ImageExchangeRateProvider =
        ImageExchangeRateProvider(
            imageExchangeRateResultProvider,
            exchangeRateAnalyzers
        )

    @Provides
    @Singleton
    @IntoMap
    @StringKey("FacebookLoginData")
    fun getFacebookLoginHandler(
        userService: UserService,
        context: Context
    ): InternalAuthorizationGateway<out LoginData> =
        FacebookLoginHandler(userService, context)

}