package cz.city.honest.view.camera.rate

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.SurfaceHolder
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.lifecycle.Observer
import cz.city.honest.dto.ExchangeRate
import cz.city.honest.dto.WatchedSubject
import cz.city.honest.view.R
import cz.city.honest.view.camera.CameraFragment
import cz.city.honest.view.camera.analyzer.ExchangeRateAnalyzer
import cz.city.honest.view.camera.result.CameraResultActivity
import cz.city.honest.view.camera.result.CameraResultActivity.Companion.EXCHANGE_RATE_RESULT


class RateCameraFragment : CameraFragment(){


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
        setViewModels()
        cameraViewModel.exchangeRateResult
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
    }

    private fun getWatchedSubject(): WatchedSubject? =
        activity!!.intent.extras?.get(RateCameraActivity.WATCHED_SUBJECT)
            .run { if (this != null) this as WatchedSubject else null }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        analyzedTextView = findAnalyzedTextView(view)
    }

    private fun showAnalyzedText(lines: List<String>) {
        val stringBuilder = StringBuilder()
        lines.forEach { stringBuilder.appendln(it) }
        analyzedTextView.text = stringBuilder.toString()
    }

    private fun findAnalyzedTextView(container: View): TextView =
        container.findViewById(R.id.analyzed_text)

    override fun getImageAnalyzer() =
        super.getImageAnalyzer()
            .also {
                it.setAnalyzer(
                    cameraExecutor,
                    ExchangeRateAnalyzer(
                        cameraViewModel,
                        this::showAnalyzedText
                    )
                )
            }
}
