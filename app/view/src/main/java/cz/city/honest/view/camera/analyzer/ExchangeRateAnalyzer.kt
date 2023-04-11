package cz.city.honest.view.camera.analyzer

import androidx.camera.core.ImageProxy
import cz.city.honest.viewmodel.CameraViewModel

class ExchangeRateAnalyzer(
    cameraViewModel: CameraViewModel,
    private val textCallback: (lines: List<String>) -> Unit
) : ImageCameraAnalyzer(cameraViewModel) {

    override fun analyze(imageProxy: ImageProxy) {
        imageProxy.image ?: return
        val analyzeImageData = getAnalyzeImageData(imageProxy)
        cameraViewModel.analyzeRate(analyzeImageData, textCallback).subscribe {
            imageProxy.close()
        }
    }

}