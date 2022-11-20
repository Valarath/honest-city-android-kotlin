package cz.city.honest.view.camera

import android.content.Intent
import android.graphics.*
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.*
import android.widget.TextView
import androidx.camera.core.AspectRatio
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import cz.city.honest.dto.ExchangeRate
import cz.city.honest.dto.WatchedSubject
import cz.city.honest.view.R
import cz.city.honest.view.camera.result.CameraResultActivity
import cz.city.honest.view.camera.result.CameraResultActivity.Companion.EXCHANGE_RATE_RESULT
import cz.city.honest.viewmodel.CameraViewModel
//import cz.honest.city.internal.provider.rate.ImageExchangeRateResultProvider
import dagger.android.support.DaggerAppCompatDialogFragment
import kotlinx.android.synthetic.main.fragment_camera.*
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import javax.inject.Inject
import kotlin.math.abs
import kotlin.math.ln
import kotlin.math.max
import kotlin.math.min


class CameraFragment : DaggerAppCompatDialogFragment(), SurfaceHolder.Callback {

    private lateinit var cameraViewModel: CameraViewModel

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    private lateinit var cameraExecutor: ExecutorService

    private lateinit var scopedExecutor: ScopedExecutor

    private lateinit var cameraProvider: ProcessCameraProvider

    lateinit var viewFinder: PreviewView

    lateinit var analyzedTextView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val root = inflater.inflate(R.layout.fragment_camera, container, false)
        also { setViewModels() }
        cameraViewModel.result
            .observe(viewLifecycleOwner,
                Observer { showCameraAnalyzerResult(it) }
            )
        return root
    }

    private fun setViewModels() =
        this.apply {
            cameraViewModel =
                ViewModelProvider(this, viewModelFactory).get(CameraViewModel::class.java)
        }

    private fun showCameraAnalyzerResult(exchangeRate: ExchangeRate?) {
        exchangeRate?.let { openCameraResultActivity(it) }
    }

    private fun openCameraResultActivity(exchangeRate: ExchangeRate) {
        val intent = Intent(activity, CameraResultActivity::class.java)
        intent.putExtra(EXCHANGE_RATE_RESULT, exchangeRate)
        intent.putExtra(CameraResultActivity.WATCHED_SUBJECT, getWatchedSubject())
        startActivity(intent)
    }

    private fun getWatchedSubject(): WatchedSubject? =
        activity!!.intent.extras?.get(CameraActivity.WATCHED_SUBJECT)
            .run { if (this != null) this as WatchedSubject else null }

    override fun onDestroyView() {
        super.onDestroyView()
        cameraExecutor.shutdown()
        scopedExecutor.shutdown()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val container = view as ConstraintLayout
        initExecutors()
        initViewFinder(container)
        initOverlay(container)
        analyzedTextView = findAnalyzedTextView(view)
    }

    private fun initExecutors() {
        cameraExecutor = Executors.newSingleThreadExecutor()
        scopedExecutor = ScopedExecutor(cameraExecutor)
    }

    private fun initViewFinder(container: ConstraintLayout) {
        viewFinder = findViewFinder(container)
        viewFinder.post { setUpCamera() }
    }

    private fun showAnalyzedText(lines: List<String>) {
        val stringBuilder = StringBuilder()
        lines.forEach { stringBuilder.appendln(it) }
        analyzedTextView.text = stringBuilder.toString()
    }

    private fun initOverlay(container: ConstraintLayout) {
        val overlay: SurfaceView = findOverlay(container)
        overlay.setZOrderOnTop(true)
        val holder = overlay.holder;
        holder.setFormat(PixelFormat.TRANSPARENT);
        holder.addCallback(this);
    }

    override fun surfaceCreated(holder: SurfaceHolder) {
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

    override fun surfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {
        drawFocusRectangle(holder)
    }

    override fun surfaceDestroyed(p0: SurfaceHolder) {
        //TODO("Not yet implemented")
    }

    private fun findViewFinder(container: View): PreviewView =
        container.findViewById(R.id.viewfinder)

    private fun findOverlay(container: View): SurfaceView =
        container.findViewById(R.id.overlay)

    private fun findAnalyzedTextView(container: View): TextView =
        container.findViewById(R.id.analyzed_text)

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


    private fun getImageAnalyzer() =
        ImageAnalysis.Builder()
            .setTargetAspectRatio(getAspectRatio())
            .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
            .setTargetRotation(getDisplayRotation())
            .build()
            .also {
                it.setAnalyzer(
                    cameraExecutor,
                    ImageCameraAnalyzer(
                        cameraViewModel,
                        this::showAnalyzedText
                    )
                )
            }

    private fun getDisplayRotation() = viewFinder.display.rotation

    private fun getAspectRatio() = DisplayMetrics()
        .also { viewFinder.display.getRealMetrics(it) }
        .run { aspectRatio(widthPixels, heightPixels) }

    private fun aspectRatio(width: Int, height: Int): Int {
        val previewRatio = ln(max(width, height).toDouble() / min(width, height))
        if (abs(previewRatio - ln(4.0 / 3.0)) <= abs(previewRatio - ln(16.0 / 9.0)))
            return AspectRatio.RATIO_4_3
        return AspectRatio.RATIO_16_9
    }

}
