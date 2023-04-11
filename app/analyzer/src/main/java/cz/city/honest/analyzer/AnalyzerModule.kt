package cz.city.honest.analyzer

import cz.city.honest.analyzer.rate.ExchangeRateAnalyzer
import cz.city.honest.analyzer.rate.RowExchangeRateAnalyzer
import cz.city.honest.analyzer.subject.SubjectAnalyzer
import cz.city.honest.analyzer.subject.FullNameSubjectAnalyzer
import cz.city.honest.service.settings.CurrencySettingsService
import cz.city.honest.service.settings.SubjectSettingsService
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
        fun getFullNameExchangeAnalyzer(subjectSettingsService: SubjectSettingsService): FullNameSubjectAnalyzer =
            FullNameSubjectAnalyzer(subjectSettingsService)

        @Provides
        @Singleton
        fun getFullNameExchangeAnalyzers(fullNameExchangeAnalyzer: FullNameSubjectAnalyzer): List<SubjectAnalyzer> =
            listOf(fullNameExchangeAnalyzer)

        @Provides
        @Singleton
        fun getExchangeRateAnalyzers(rowExchangeRateAnalyzer: RowExchangeRateAnalyzer): List<ExchangeRateAnalyzer> =
            listOf(rowExchangeRateAnalyzer)
    }
}