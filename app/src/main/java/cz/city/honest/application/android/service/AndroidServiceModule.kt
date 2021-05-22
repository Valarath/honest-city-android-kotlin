package cz.city.honest.application.android.service

import android.content.Context
import cz.city.honest.application.android.service.provider.AndroidPositionProvider
import cz.city.honest.application.android.service.provider.AndroidUserProvider
import cz.city.honest.application.android.service.provider.rate.ImageExchangeRateProvider
import cz.city.honest.application.android.service.provider.rate.ImageExchangeRateResultProvider
import cz.city.honest.application.model.service.PositionProvider
import cz.city.honest.application.model.service.UserProvider
import cz.city.honest.application.model.service.settings.CurrencySettingsService
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
    fun getUserProvider(): UserProvider = AndroidUserProvider()

    @Provides
    @Singleton
    fun getImageExchangeRateResultProvider(): ImageExchangeRateResultProvider =
        ImageExchangeRateResultProvider()

    @Provides
    @Singleton
    fun getImageExchangeRateProvider(
        currencySettingsService: CurrencySettingsService,
        imageExchangeRateResultProvider: ImageExchangeRateResultProvider
    ): ImageExchangeRateProvider = ImageExchangeRateProvider(currencySettingsService, imageExchangeRateResultProvider)

}