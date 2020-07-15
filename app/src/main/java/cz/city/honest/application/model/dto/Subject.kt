package cz.city.honest.mobile.model.dto

import cz.city.honest.application.model.dto.Suggestion
import java.io.Serializable
import java.time.LocalDate

enum class HonestyStatus(private val nextLevelOfHonesty: HonestyStatus?) : Serializable {
    HONEST(null),
    HONEST_WITH_RESERVE(HONEST),
    BE_CAUTION(HONEST_WITH_RESERVE),
    DISHONEST(BE_CAUTION),
    UNKNOWN(null);
}

open class WatchedSubject(
    open val id: Long,
    open val watchedTo: LocalDate,
    open val honestyStatus: HonestyStatus,
    open val suggestions: List<Suggestion>
) : Serializable

data class Position(val longitude: Double, val latitude: Double) : Serializable

open class ImmobileWatchedSubject(
    override val id: Long,
    override val watchedTo: LocalDate,
    override val honestyStatus: HonestyStatus,
    open val position: Position,
    override val suggestions: List<Suggestion>
) : WatchedSubject(id, watchedTo, honestyStatus, suggestions)