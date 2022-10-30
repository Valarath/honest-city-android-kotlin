package cz.city.honest.view.camera

import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import cz.city.honest.dto.AnalyzeImageData
import cz.city.honest.viewmodel.CameraViewModel

class ImageCameraAnalyzer(
    private val cameraViewModel: CameraViewModel,
    private val textCallback: (lines: List<String>) -> Unit
) : ImageAnalysis.Analyzer {

    override fun analyze(imageProxy: ImageProxy) {
        imageProxy.image ?: return
        val analyzeImageData = getAnalyzeImageData(imageProxy)
        cameraViewModel.analyze(analyzeImageData,textCallback).subscribe {
            imageProxy.close()
        }
    }

    private fun getAnalyzeImageData(imageProxy: ImageProxy) = AnalyzeImageData(
        rotationDegrees = imageProxy.imageInfo.rotationDegrees,
        height = imageProxy.height,
        width = imageProxy.width,
        format = imageProxy.format,
        image = imageProxy.image!!
    )
}