package cz.city.honest.viewmodel

import androidx.lifecycle.LiveDataReactiveStreams
import cz.city.honest.dto.AnalyzeImageData
import cz.city.honest.dto.ExchangeRate
import cz.city.honest.service.analyze.AnalyticService
import io.reactivex.rxjava3.core.BackpressureStrategy
import javax.inject.Inject

class CameraViewModel @Inject constructor(private val analyticService: AnalyticService) :
    ScheduledObservableViewModel() {

    val result = LiveDataReactiveStreams.fromPublisher<ExchangeRate>(getAnalyticResult())

    fun analyze(imageData: AnalyzeImageData, textCallback: (lines: List<String>) -> Unit) =
        analyticService.analyze(imageData, textCallback)

    private fun getAnalyticResult() = scheduleFlowable().flatMap {analyticService.getResult().toFlowable(BackpressureStrategy.LATEST)  }

}