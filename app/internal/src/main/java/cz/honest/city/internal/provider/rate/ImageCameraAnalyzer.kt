package cz.honest.city.internal.provider.rate

import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import com.google.mlkit.vision.common.InputImage
import cz.city.honest.dto.AnalyzeImageData

class ImageCameraAnalyzer(private val imageExchangeRateProvider: ImageExchangeRateProvider, private val textCallback:(lines:List<String>)->Unit) : ImageAnalysis.Analyzer {

    override fun analyze(imageProxy: ImageProxy) {
        val image = imageProxy.image?:return
        //TODO tady to rozdel, Data pro input image musi byt v prepravce, aneb tady se vytvori ta prepravka a posle se pres vrstvy do internal modulu, tohle bude ve view
        val inputImage = InputImage.fromBitmap(ImageUtils.getInputImage(imageProxy, image), 0)
        imageExchangeRateProvider.provide(inputImage,textCallback )
            .addOnCompleteListener { imageProxy.close()}
    }
}