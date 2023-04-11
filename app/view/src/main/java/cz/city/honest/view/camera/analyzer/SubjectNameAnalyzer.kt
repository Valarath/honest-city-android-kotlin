package cz.city.honest.view.camera.analyzer

import androidx.camera.core.ImageProxy
import cz.city.honest.viewmodel.CameraViewModel

class SubjectNameAnalyzer(
    cameraViewModel: CameraViewModel,
) : ImageCameraAnalyzer(cameraViewModel) {

    override fun analyze(imageProxy: ImageProxy) {
        imageProxy.image ?: return
        val analyzeImageData = getAnalyzeImageData(imageProxy)
        cameraViewModel.analyzeName(analyzeImageData,{}).subscribe {
            imageProxy.close()
        }
    }

}