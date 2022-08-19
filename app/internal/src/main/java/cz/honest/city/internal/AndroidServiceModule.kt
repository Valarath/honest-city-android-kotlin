package cz.honest.city.internal

import android.content.Context
import cz.honest.city.internal.filter.FilterSharedPreferenceRepository
import cz.honest.city.internal.provider.AndroidPositionProvider
import cz.honest.city.internal.provider.AndroidUserProvider
import cz.city.honest.analyzer.ExchangeRateAnalyzer
import cz.honest.city.internal.provider.rate.ImageExchangeRateProvider
import cz.honest.city.internal.provider.rate.ImageExchangeRateResultProvider
import cz.city.honest.service.user.UserProvider
import cz.city.honest.service.user.UserService
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class AndroidServiceModule {

    @Provides
    @Singleton
    fun getPositionProvider(context: Context): cz.city.honest.service.subject.PositionProvider = AndroidPositionProvider(context)


    @Provides
    @Singleton
    fun getFilterPersistenceHandler(context: Context): cz.city.honest.service.filter.FilterPersistenceHandler =
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
        exchangeRateAnalyzers: List<ExchangeRateAnalyzer>
    ): ImageExchangeRateProvider =
        ImageExchangeRateProvider(
            imageExchangeRateResultProvider,
            exchangeRateAnalyzers
        )

}