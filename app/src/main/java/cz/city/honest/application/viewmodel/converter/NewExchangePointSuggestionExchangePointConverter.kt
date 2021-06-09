package cz.city.honest.application.viewmodel.converter

import cz.city.honest.application.model.dto.NewExchangePointSuggestion
import cz.city.honest.mobile.model.dto.ExchangePoint
import cz.city.honest.mobile.model.dto.ExchangeRate
import cz.city.honest.mobile.model.dto.HonestyStatus
import cz.city.honest.mobile.model.dto.Watched
import java.time.LocalDate

class NewExchangePointSuggestionExchangePointConverter {

    companion object{
        fun convert(newExchangePointSuggestion: NewExchangePointSuggestion) =
            ExchangePoint(
                id = getId(),
                watchedTo = LocalDate.now(),
                exchangePointRate = createEmptyExchangeRate(),
                suggestions = mutableListOf(newExchangePointSuggestion),
                image = "aaa".toByteArray(),
                honestyStatus = HonestyStatus.UNKNOWN,
                position = newExchangePointSuggestion.position
            )

        private fun createEmptyExchangeRate() = ExchangeRate(
            id = "",
            rates = mutableSetOf(),
            watched = createEmptyWatched()
        )

        private fun createEmptyWatched() = Watched(
            from = LocalDate.now(),
            to = LocalDate.now()
        )

        fun getId(): String = NewExchangePointSuggestion::class.java.simpleName
    }
}