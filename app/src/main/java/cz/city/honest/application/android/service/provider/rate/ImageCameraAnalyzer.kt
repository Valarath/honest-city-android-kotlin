package cz.city.honest.application.android.service.provider.rate

import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import com.google.mlkit.vision.common.InputImage

class ImageCameraAnalyzer(private val imageExchangeRateProvider: ImageExchangeRateProvider) : ImageAnalysis.Analyzer {

    override fun analyze(imageProxy: ImageProxy) {
        val image = imageProxy.image?:return
        imageExchangeRateProvider.provide(InputImage.fromMediaImage(image,0))
            .addOnCompleteListener {  imageProxy.close()}
    }
}