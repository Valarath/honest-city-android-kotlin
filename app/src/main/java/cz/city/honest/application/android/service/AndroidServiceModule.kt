package cz.city.honest.application.android.service

import android.content.Context
import cz.city.honest.application.analyzer.ExchangeRateAnalyzer
import cz.city.honest.application.android.service.filter.FilterSharedPreferenceRepository
import cz.city.honest.application.android.service.provider.AndroidPositionProvider
import cz.city.honest.application.android.service.provider.AndroidUserProvider
import cz.city.honest.application.android.service.provider.rate.ImageExchangeRateProvider
import cz.city.honest.application.android.service.provider.rate.ImageExchangeRateResultProvider
import cz.city.honest.application.model.service.UserProvider
import cz.city.honest.application.model.service.UserService
import cz.city.honest.application.model.service.filter.FilterPersistenceHandler
import cz.city.honest.application.model.service.subject.PositionProvider
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class AndroidServiceModule {

    @Provides
    @Singleton
    fun getPositionProvider(context: Context): PositionProvider = AndroidPositionProvider(context)


    @Provides
    @Singleton
    fun getFilterPersistenceHandler(context: Context): FilterPersistenceHandler =
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
        exchangeRateAnalyzers: @JvmSuppressWildcards List<ExchangeRateAnalyzer>
    ): ImageExchangeRateProvider =
        ImageExchangeRateProvider(
            imageExchangeRateResultProvider,
            exchangeRateAnalyzers
        )

}