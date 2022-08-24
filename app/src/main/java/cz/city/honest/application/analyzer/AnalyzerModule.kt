package cz.city.honest.application.analyzer

import cz.city.honest.application.model.service.settings.CurrencySettingsService
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class AnalyzerModule {

    @Provides
    @Singleton
    fun getRowExchangeRateAnalyzer(currencySettingsService: CurrencySettingsService):RowExchangeRateAnalyzer =
        RowExchangeRateAnalyzer(currencySettingsService)

    @Provides
    @Singleton
    fun getExchangeRateAnalyzers(rowExchangeRateAnalyzer: RowExchangeRateAnalyzer): List<ExchangeRateAnalyzer> =
        listOf(rowExchangeRateAnalyzer)
}