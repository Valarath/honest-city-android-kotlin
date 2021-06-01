package cz.city.honest.application.view.camera

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import cz.city.honest.application.R
import cz.city.honest.application.android.service.provider.rate.ImageCameraAnalyzer
import cz.city.honest.application.android.service.provider.rate.ImageExchangeRateProvider
import cz.city.honest.application.android.service.provider.rate.ImageExchangeRateResultProvider
import cz.city.honest.application.view.camera.result.CameraResultActivity
import cz.city.honest.application.view.camera.result.CameraResultActivity.Companion.EXCHANGE_RATE_RESULT
import cz.city.honest.application.view.camera.result.CameraResultFragment
import cz.city.honest.application.view.component.rate.ExchangeRateTable
import cz.city.honest.application.view.component.rate.ExchangeRateTableData
import cz.city.honest.mobile.model.dto.ExchangeRate
import dagger.android.support.DaggerAppCompatDialogFragment
import kotlinx.android.synthetic.main.fragment_camera.*
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import javax.inject.Inject


class CameraFragment : DaggerAppCompatDialogFragment() {

    @Inject
    lateinit var imageExchangeRateResultProvider: ImageExchangeRateResultProvider

    @Inject
    lateinit var imageExchangeRateProvider: ImageExchangeRateProvider

    private lateinit var cameraExecutor: ExecutorService

    private lateinit var scopedExecutor: ScopedExecutor

    private lateinit var cameraProvider: ProcessCameraProvider

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val root = inflater.inflate(R.layout.fragment_camera, container, false)
        imageExchangeRateResultProvider.result.observe(viewLifecycleOwner, Observer {

            val intent = Intent(activity, CameraResultActivity::class.java)
            intent.putExtra(EXCHANGE_RATE_RESULT,it )
            startActivity(intent)
        })
        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()

        // Shut down our background executor
        cameraExecutor.shutdown()
        scopedExecutor.shutdown()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val container = view as ConstraintLayout
        initExecutors()
        initViewFinder(container)
    }

    private fun initExecutors() {
        cameraExecutor = Executors.newSingleThreadExecutor()
        scopedExecutor = ScopedExecutor(cameraExecutor)
    }

    private fun initViewFinder(container: ConstraintLayout) {
        val viewFinder: PreviewView = findViewFinder(container)
        viewFinder.visibility = View.VISIBLE
        viewFinder.post { setUpCamera() }
    }

    private fun findViewFinder(container: View): PreviewView =
        container.findViewById(R.id.viewfinder)


    private fun setUpCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(requireContext())
        cameraProviderFuture.addListener(Runnable {
            cameraProvider = cameraProviderFuture.get()
            bindCameraUseCases()
        }, ContextCompat.getMainExecutor(requireContext()))
    }

    private fun bindCameraUseCases() {

        val preview = Preview.Builder()
            .build()
            .apply { setSurfaceProvider(viewfinder.surfaceProvider) }

        try {
            cameraProvider.unbindAll()
            cameraProvider.bindToLifecycle(this, CameraSelector.DEFAULT_BACK_CAMERA, preview, getImageAnalyzer())
        } catch (exc: IllegalStateException) {
            println(exc)
        }
    }

    private fun getImageAnalyzer() =
        ImageAnalysis.Builder()
            // We request aspect ratio but no resolution
            .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
            .build()
            .also {
                it.setAnalyzer(
                    cameraExecutor
                    , ImageCameraAnalyzer(imageExchangeRateProvider)
                )
            }

}
