package cz.city.honest.application.model.dto

import java.time.LocalDate

enum class HonestyStatus(private val nextLevelOfHonesty: HonestyStatus?) : HonestCitySerializable {
    HONEST(null),
    HONEST_WITH_RESERVE(HONEST),
    BE_CAUTION(HONEST_WITH_RESERVE),
    DISHONEST(BE_CAUTION),
    UNKNOWN(null);
}

open class WatchedSubject(
    open val id: String,
    open val watchedTo: LocalDate?,
    open val honestyStatus: HonestyStatus,
    open val suggestions: List<Suggestion>
) : HonestCitySerializable

data class Position(val longitude: Double, val latitude: Double) : HonestCitySerializable

open class ImmobileWatchedSubject(
    override val id: String,
    override val watchedTo: LocalDate?,
    override val honestyStatus: HonestyStatus,
    open val position: Position,
    override val suggestions: List<Suggestion>
) : WatchedSubject(id, watchedTo, honestyStatus, suggestions)