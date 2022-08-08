package cz.city.honest.application.view.camera.result

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import cz.city.honest.application.R
import cz.city.honest.application.model.dto.ExchangeRate
import cz.city.honest.application.model.dto.WatchedSubject
import cz.city.honest.application.view.component.rate.ExchangeRateTable
import cz.city.honest.application.view.component.rate.ExchangeRateTableData
import cz.city.honest.application.viewmodel.CameraResultViewModel
import dagger.android.support.DaggerAppCompatDialogFragment
import javax.inject.Inject

class CameraResultFragment : DaggerAppCompatDialogFragment() {

    private lateinit var cameraResultViewModel: CameraResultViewModel

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        cameraResultViewModel =
            ViewModelProvider(this, viewModelFactory).get(CameraResultViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val root: View = inflater.inflate(R.layout.fragment_camera_result, container, false)
        val exchangeRate = root.findViewById<ExchangeRateTable>(R.id.exchange_rate)
        cameraResultViewModel.authorityRate.observe(viewLifecycleOwner, Observer {
            exchangeRate.showExchangePointRates(getExchangeRateTableData(it,root))
        })
        initSuggestNewRateButton(root)
        return root
    }

    private fun initSuggestNewRateButton(
        root: View
    ) = root.findViewById<Button>(R.id.suggest_new_rate)
        .also { setSuggestButtonVisibility(it) }
        .apply {this.setOnClickListener { suggestNewResult(this) } }

    private fun suggestNewResult(button: Button) {
        cameraResultViewModel.suggest(getWatchedSubject()!!.id, getExchangeRateResult())
        button.visibility = View.GONE
    }

    @SuppressLint("FragmentLiveDataObserve")
    private fun setSuggestButtonVisibility(button: Button) {
        cameraResultViewModel.loggedUser.observe(this, Observer {
            if (getWatchedSubject() != null)
                button.visibility = View.VISIBLE
        })
    }

    private fun getWatchedSubject() = activity!!.intent.extras!![CameraResultActivity.WATCHED_SUBJECT]
        .run { if (this != null) this as WatchedSubject else null }

    private fun getExchangeRateTableData(
        authorityRate: ExchangeRate,
        root: View
    ) = ExchangeRateTableData(
        authorityRate = authorityRate,
        exchangePointRate = getExchangeRateResult(),
        root = root
    )

    private fun getExchangeRateResult() =
        activity!!.intent.extras!![CameraResultActivity.EXCHANGE_RATE_RESULT] as ExchangeRate

}