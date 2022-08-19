package cz.city.honest.viewmodel.converter

import cz.city.honest.dto.*
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