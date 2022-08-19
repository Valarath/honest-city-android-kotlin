package cz.city.honest.view.camera

import android.content.Intent
import android.graphics.Color
import android.graphics.Paint
import android.graphics.PixelFormat
import android.graphics.PorterDuff
import android.os.Bundle
import android.view.*
import android.widget.TextView
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import cz.city.honest.view.R
import cz.honest.city.internal.provider.rate.ImageCameraAnalyzer
import cz.honest.city.internal.provider.rate.ImageExchangeRateProvider
import cz.honest.city.internal.provider.rate.ImageExchangeRateResultProvider
import cz.city.honest.dto.ExchangeRate
import cz.city.honest.dto.WatchedSubject
import cz.city.honest.view.camera.result.CameraResultActivity
import cz.city.honest.view.camera.result.CameraResultActivity.Companion.EXCHANGE_RATE_RESULT
import dagger.android.support.DaggerAppCompatDialogFragment
import kotlinx.android.synthetic.main.fragment_camera.*
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import javax.inject.Inject


class CameraFragment : DaggerAppCompatDialogFragment(),SurfaceHolder.Callback {

    @Inject
    lateinit var imageExchangeRateResultProvider: ImageExchangeRateResultProvider

    @Inject
    lateinit var imageExchangeRateProvider: ImageExchangeRateProvider

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
        imageExchangeRateResultProvider.result
            .observe(viewLifecycleOwner,
                Observer { showCameraAnalyzerResult(it) }
            )
        return root
    }

    private fun showCameraAnalyzerResult(exchangeRate: ExchangeRate?) {
        exchangeRate?.let { openCameraResultActivity(it) }
    }

    private fun openCameraResultActivity(exchangeRate: ExchangeRate) {
        val intent = Intent(activity, CameraResultActivity::class.java)
        intent.putExtra(EXCHANGE_RATE_RESULT, exchangeRate)
        intent.putExtra(CameraResultActivity.WATCHED_SUBJECT, getWatchedSubject())
        startActivity(intent)
        imageExchangeRateResultProvider.result.postValue(null)
    }

    private fun getWatchedSubject(): WatchedSubject? = activity!!.intent.extras?.get(CameraActivity.WATCHED_SUBJECT)
        .run { if(this != null)this as WatchedSubject else null }

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

    private fun showAnalyzedText(lines:List<String>){
        val stringBuilder = StringBuilder()
        lines.forEach {stringBuilder.appendln(it)  }
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

    private fun DrawFocusRect(color: Int,holder: SurfaceHolder) {
        val height: Int = viewFinder.height
        val width: Int = viewFinder.width

        var diameter = width
        if (height < width) {
            diameter = height
        }
        val offset = (0.05 * diameter).toInt()
        diameter -= offset
        val canvas = holder.lockCanvas()
        canvas.drawColor(0, PorterDuff.Mode.CLEAR)
        //border's properties
        val paint = Paint()
        paint.style = Paint.Style.STROKE
        paint.color = color
        paint.strokeWidth = 5f
        val left = width / 2 - diameter / 3
        val top = height / 2 - diameter / 3
        val right = width / 2 + diameter / 3
        val bottom = height / 2 + diameter / 3

        canvas.drawRect(left.toFloat(), top.toFloat(), right.toFloat(), bottom.toFloat(), paint)
        //canvas.drawRect(viewFinder.left.toFloat(), viewFinder.top.toFloat(), viewFinder.right.toFloat(), viewFinder.bottom.toFloat(), paint)
        holder.unlockCanvasAndPost(canvas)
    }

    override fun surfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {
        DrawFocusRect(Color.parseColor("#b3dabb"),holder)
    }

    override fun surfaceDestroyed(p0: SurfaceHolder) {
        TODO("Not yet implemented")
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
            cameraProvider.bindToLifecycle(this, CameraSelector.DEFAULT_BACK_CAMERA, getPreview(), getImageAnalyzer())
        } catch (exc: IllegalStateException) {
            println(exc)
        }
    }

    private fun getPreview() =
        Preview.Builder()
            .build()
            .apply { setSurfaceProvider(viewfinder.surfaceProvider) }


    private fun getImageAnalyzer() =
        ImageAnalysis.Builder()
            .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
            .build()
            .also {
                it.setAnalyzer(
                    cameraExecutor
                    ,
                    ImageCameraAnalyzer(
                        imageExchangeRateProvider,
                        this::showAnalyzedText
                    )
                )
            }

}
