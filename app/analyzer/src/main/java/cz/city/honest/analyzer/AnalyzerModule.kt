package cz.city.honest.analyzer

import cz.city.honest.service.settings.CurrencySettingsService
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class AnalyzerModule {
    companion object {
        @Provides
        @Singleton
        fun getRowExchangeRateAnalyzer(currencySettingsService: CurrencySettingsService): RowExchangeRateAnalyzer =
            RowExchangeRateAnalyzer(currencySettingsService)

        @Provides
        @Singleton
        fun getExchangeRateAnalyzers(rowExchangeRateAnalyzer: RowExchangeRateAnalyzer): List<ExchangeRateAnalyzer> =
            listOf(rowExchangeRateAnalyzer)
    }
}