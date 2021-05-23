package cz.city.honest.application.android.service.provider.rate

import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import com.google.mlkit.vision.common.InputImage

class ImageCameraAnalyzer(private val imageExchangeRateProvider: ImageExchangeRateProvider) : ImageAnalysis.Analyzer {

    override fun analyze(imageProxy: ImageProxy) {
        val image = imageProxy.image?:return
        val inputImage = InputImage.fromBitmap(ImageUtils.getInputImage(imageProxy,image), 0)
        imageExchangeRateProvider.provide(inputImage)
            .addOnCompleteListener {  imageProxy.close()}
    }
}