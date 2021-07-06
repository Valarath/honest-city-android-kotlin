package cz.city.honest.application.viewmodel

import androidx.lifecycle.MutableLiveData
import cz.city.honest.application.model.dto.ExchangeRate
import cz.city.honest.application.model.dto.ExchangeRateSuggestion
import cz.city.honest.application.model.dto.State
import cz.city.honest.application.model.dto.UserSuggestionStateMarking
import cz.city.honest.application.model.service.authority.AuthorityService
import cz.city.honest.application.model.service.suggestion.SuggestionService
import java.util.*
import javax.inject.Inject

class CameraResultViewModel @Inject constructor(
    private val authorityService: AuthorityService,
    private val suggestionService: SuggestionService
) :
    ScheduledViewModel() {

    val authorityRate = MutableLiveData<ExchangeRate>()

    init {
        schedule {
            authorityService.getAuthority().subscribe {
                authorityRate.postClearValue(it)
            }
        }
    }

    fun suggest(subjectId: String, exchangeRate: ExchangeRate) = suggestionService
        .createSuggestion(
            getNewExchangeRateSuggestion(subjectId, exchangeRate),
            UserSuggestionStateMarking.NEW
        )
        .subscribe()


    private fun getNewExchangeRateSuggestion(subjectId: String, exchangeRate: ExchangeRate) =
        ExchangeRateSuggestion(
            id = UUID.randomUUID().toString(),
            state = State.IN_PROGRESS,
            subjectId =subjectId,
            votes = 1,
            suggestedExchangeRate =exchangeRate
        )

}