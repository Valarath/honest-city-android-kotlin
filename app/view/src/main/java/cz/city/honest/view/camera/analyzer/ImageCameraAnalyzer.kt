package cz.city.honest.view.camera.analyzer

import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import cz.city.honest.dto.AnalyzeImageData
import cz.city.honest.viewmodel.CameraViewModel

abstract class ImageCameraAnalyzer(
    protected val cameraViewModel: CameraViewModel,
) : ImageAnalysis.Analyzer {

    protected fun getAnalyzeImageData(imageProxy: ImageProxy) = AnalyzeImageData(
        rotationDegrees = imageProxy.imageInfo.rotationDegrees,
        height = imageProxy.height,
        width = imageProxy.width,
        format = imageProxy.format,
        image = imageProxy.image!!
    )
}