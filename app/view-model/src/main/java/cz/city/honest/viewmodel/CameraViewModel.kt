package cz.city.honest.viewmodel

import androidx.lifecycle.LiveDataReactiveStreams
import cz.city.honest.dto.AnalyzeImageData
import cz.city.honest.dto.ExchangeRate
import cz.city.honest.service.analyze.AnalyticService
import io.reactivex.rxjava3.core.BackpressureStrategy
import javax.inject.Inject

class CameraViewModel @Inject constructor(private val analyticService: AnalyticService) :
    ScheduledObservableViewModel() {

    val exchangeRateResult = LiveDataReactiveStreams.fromPublisher<ExchangeRate>(getExchangeRateAnalyticResult())
    val nameResult = LiveDataReactiveStreams.fromPublisher<String>(getNameAnalyticResult())

    fun analyzeRate(imageData: AnalyzeImageData, textCallback: (lines: List<String>) -> Unit) =
        analyticService.analyzeRate(imageData, textCallback)

    private fun getExchangeRateAnalyticResult() = scheduleFlowable().flatMap {analyticService.getRateResult().toFlowable(BackpressureStrategy.LATEST)  }

    fun analyzeName(imageData: AnalyzeImageData, textCallback: (lines: List<String>) -> Unit) =
        analyticService.analyzeName(imageData, textCallback)

    private fun getNameAnalyticResult() = scheduleFlowable().flatMap {analyticService.getNameResult().toFlowable(BackpressureStrategy.LATEST)  }

}