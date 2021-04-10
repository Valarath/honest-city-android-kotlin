package cz.city.honest.application.viewmodel

import androidx.lifecycle.MutableLiveData
import cz.city.honest.application.model.dto.NewExchangePointSuggestion
import cz.city.honest.application.model.service.SubjectService
import cz.city.honest.application.model.service.SuggestionService
import cz.city.honest.application.model.service.UserSuggestionService
import cz.city.honest.mobile.model.dto.*
import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.core.Maybe
import io.reactivex.rxjava3.core.Observable
import java.time.LocalDate
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class MapViewModel @Inject constructor(
    var subjectService: SubjectService, var suggestionService: SuggestionService
) : ScheduledViewModel() {

    val watchedSubjects: MutableLiveData<List<WatchedSubject>> = MutableLiveData()

    init {
        schedule {
            getSubjects().subscribe {
                watchedSubjects.postClearValue(it)
            }
        }
    }

    private fun getSubjects() =
        Flowable.merge(subjectService.getSubjects(), getSuggestionSubjects())
            .toList()
            .toObservable()

    private fun getSuggestionSubjects() =
        suggestionService.getSuggestions(NewExchangePointSuggestion::class.java)
            .map {
                ExchangePoint(
                    id = "",
                    watchedTo = LocalDate.now(),
                    exchangePointRate = createEmptyExchangeRate(),
                    suggestions = mutableListOf(it),
                    image = "aaa".toByteArray(),
                    honestyStatus = HonestyStatus.UNKNOWN,
                    position = it.position
                )
            }

    private fun createEmptyExchangeRate() = ExchangeRate(
        id = "",
        rates = mutableSetOf(),
        watched = createEmptyWatched()
    )

    private fun createEmptyWatched() = Watched(
        from = LocalDate.now(),
        to = LocalDate.now()
    )

}
