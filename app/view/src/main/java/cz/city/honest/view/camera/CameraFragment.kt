package cz.city.honest.view.camera

import android.graphics.*
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.*
import androidx.camera.core.AspectRatio
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import cz.city.honest.view.R
import cz.city.honest.viewmodel.CameraViewModel
import dagger.android.support.DaggerAppCompatDialogFragment
import kotlinx.android.synthetic.main.fragment_camera.*
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import javax.inject.Inject
import kotlin.math.abs
import kotlin.math.ln
import kotlin.math.max
import kotlin.math.min

abstract class CameraFragment : DaggerAppCompatDialogFragment(), SurfaceHolder.Callback {

    protected lateinit var cameraViewModel: CameraViewModel

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    protected lateinit var cameraExecutor: ExecutorService

    private lateinit var scopedExecutor: ScopedExecutor

    private lateinit var cameraProvider: ProcessCameraProvider

    lateinit var viewFinder: PreviewView

    protected fun setViewModels() =
        this.apply {
            cameraViewModel =
                ViewModelProvider(this, viewModelFactory).get(CameraViewModel::class.java)
        }

    override fun surfaceCreated(holder: SurfaceHolder) {
    }

    override fun surfaceDestroyed(holder: SurfaceHolder) {
    }

    override fun onDestroyView() {
        super.onDestroyView()
        cameraExecutor.shutdown()
        scopedExecutor.shutdown()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initComponents()
    }

    override fun surfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {
        drawFocusRectangle(holder)
    }

    private fun initComponents() {
        val container = view as ConstraintLayout
        initExecutors()
        initViewFinder(container)
        initOverlay(container)
    }

    private fun initExecutors() {
        cameraExecutor = Executors.newSingleThreadExecutor()
        scopedExecutor = ScopedExecutor(cameraExecutor)
    }

    private fun initViewFinder(container: ConstraintLayout) {
        viewFinder = findViewFinder(container)
        viewFinder.post { setUpCamera() }
    }

    private fun initOverlay(container: ConstraintLayout) {
        val overlay: SurfaceView = findOverlay(container)
        overlay.setZOrderOnTop(true)
        val holder = overlay.holder;
        holder.setFormat(PixelFormat.TRANSPARENT);
        holder.addCallback(this);
    }

    private fun drawFocusRectangle(holder: SurfaceHolder) {
        val height: Int = viewFinder.height
        val width: Int = viewFinder.width
        val canvas = holder.lockCanvas()
        canvas.drawColor(0, PorterDuff.Mode.CLEAR)
        drawOnCanvas(canvas, height, width)
        holder.unlockCanvasAndPost(canvas)
    }

    private fun drawOnCanvas(canvas: Canvas, height: Int, width: Int) {
        val diameter = getDiameter(height, width)
        canvas.drawRect(
            (width / 2 - diameter / 3).toFloat(),
            (height / 2 - diameter / 3).toFloat(),
            (width / 2 + diameter / 3).toFloat(),
            (height / 2 + diameter / 3).toFloat(),
            getBorderPaint()
        )
    }

    private fun getBorderPaint() = Paint()
        .also {
            it.style = Paint.Style.STROKE
            it.color = Color.parseColor("#b3dabb")
            it.strokeWidth = 5f
        }

    private fun getDiameter(height: Int, width: Int): Int {
        var diameter = width
        if (height < width)
            diameter = height
        val offset = (0.05 * diameter).toInt()
        diameter -= offset
        return diameter
    }

    private fun findViewFinder(container: View): PreviewView =
        container.findViewById(R.id.viewfinder)

    private fun findOverlay(container: View): SurfaceView =
        container.findViewById(R.id.overlay)

    private fun setUpCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(requireContext())
        cameraProviderFuture.addListener(Runnable {
            cameraProvider = cameraProviderFuture.get()
            bindCameraUseCases()
        }, ContextCompat.getMainExecutor(requireContext()))
    }

    private fun bindCameraUseCases() {
        try {
            cameraProvider.unbindAll()
            cameraProvider.bindToLifecycle(
                this,
                CameraSelector.DEFAULT_BACK_CAMERA,
                getPreview(),
                getImageAnalyzer()
            )
        } catch (exc: IllegalStateException) {
            println(exc)
        }
    }

    private fun getPreview() =
        Preview.Builder()
            .setTargetAspectRatio(getAspectRatio())
            .setTargetRotation(getDisplayRotation())
            .build()
            .apply { setSurfaceProvider(viewfinder.surfaceProvider) }

    protected open fun getImageAnalyzer() =
        ImageAnalysis.Builder()
            .setTargetAspectRatio(getAspectRatio())
            .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
            .setTargetRotation(getDisplayRotation())
            .build()

    private fun getDisplayRotation() = viewFinder.display.rotation

    private fun getAspectRatio() = DisplayMetrics()
        .also { viewFinder = findViewFinder(view as ConstraintLayout) }
        .also { viewFinder.display.getRealMetrics(it) }
        .run { aspectRatio(widthPixels, heightPixels) }

    private fun aspectRatio(width: Int, height: Int): Int {
        val previewRatio = ln(max(width, height).toDouble() / min(width, height))
        if (abs(previewRatio - ln(4.0 / 3.0)) <= abs(previewRatio - ln(16.0 / 9.0)))
            return AspectRatio.RATIO_4_3
        return AspectRatio.RATIO_16_9
    }

}