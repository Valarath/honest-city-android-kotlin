package cz.city.honest.application.model.repository

import cz.city.honest.application.model.dto.ExchangeRateSuggestion
import cz.city.honest.application.model.repository.suggestion.SuggestionRepository
import dagger.Binds
import dagger.Module
import dagger.multibindings.ClassKey
import dagger.multibindings.IntoMap

@Module
abstract class TestConfiguration(){

    @Binds
    @IntoMap
    @ClassKey(ExchangeRateSuggestion::class)
    internal abstract fun getExchangeRateSuggestionsRepository(
        exchangeRateRepository: SuggestionRepository<out ExchangeRateSuggestion>
    ): SuggestionRepository<out ExchangeRateSuggestion>

}