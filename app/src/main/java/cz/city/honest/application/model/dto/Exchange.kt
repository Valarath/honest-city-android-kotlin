package cz.city.honest.application.model.dto

import java.time.LocalDate

data class Watched(val from: LocalDate, val to: LocalDate?) : HonestCitySerializable

open class ExchangeRateValues(open val buy: Double) : HonestCitySerializable

data class Rate(val currency: String, val rateValues: ExchangeRateValues) : HonestCitySerializable

data class ExchangeRate(val id: String, val watched: Watched, val rates: MutableSet<Rate>) :
    HonestCitySerializable

data class ExchangePointRateValues(override val buy: Double, val sell: Double) :
    ExchangeRateValues(buy)

data class ExchangePoint(
    override val id: String,
    override val watchedTo: LocalDate,
    override val honestyStatus: HonestyStatus,
    override val position: Position,
    override val suggestions: MutableList<Suggestion>,
    val exchangePointRate: ExchangeRate?,
    val image: ByteArray
) : ImmobileWatchedSubject(id, watchedTo, honestyStatus, position, suggestions) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as ExchangePoint

        if (id != other.id) return false
        if (watchedTo != other.watchedTo) return false
        if (honestyStatus != other.honestyStatus) return false
        if (position != other.position) return false
        if (suggestions != other.suggestions) return false
        if (exchangePointRate != other.exchangePointRate) return false
        if (!image.contentEquals(other.image)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id.hashCode()
        result = 31 * result + watchedTo.hashCode()
        result = 31 * result + honestyStatus.hashCode()
        result = 31 * result + position.hashCode()
        result = 31 * result + suggestions.hashCode()
        result = 31 * result + exchangePointRate.hashCode()
        result = 31 * result + image.contentHashCode()
        return result
    }
}
