package cz.city.honest.analyzer

import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class AnalyzerModule {

    @Provides
    @Singleton
    fun getRowExchangeRateAnalyzer(currencySettingsService: cz.city.honest.service.settings.CurrencySettingsService) =
        RowExchangeRateAnalyzer(currencySettingsService)

    @Provides
    @Singleton
    fun getExchangeRateAnalyzers(rowExchangeRateAnalyzer: RowExchangeRateAnalyzer): List<ExchangeRateAnalyzer> =
        listOf(rowExchangeRateAnalyzer)
}