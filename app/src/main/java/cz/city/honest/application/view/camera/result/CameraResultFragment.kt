package cz.city.honest.application.view.camera.result

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import cz.city.honest.application.R
import cz.city.honest.application.view.component.rate.ExchangeRateTable
import cz.city.honest.application.view.component.rate.ExchangeRateTableData
import cz.city.honest.application.viewmodel.CameraResultViewModel
import cz.city.honest.mobile.model.dto.ExchangeRate
import dagger.android.support.DaggerAppCompatDialogFragment
import javax.inject.Inject

class CameraResultFragment : DaggerAppCompatDialogFragment() {

    private lateinit var cameraResultViewModel: CameraResultViewModel

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        cameraResultViewModel = ViewModelProvider(this, viewModelFactory).get(CameraResultViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val root = inflater.inflate(R.layout.exchange_rate_table, container, false)
        cameraResultViewModel.authorityRate.observe(viewLifecycleOwner, Observer{
            ExchangeRateTable(activity!!, getExchangeRateTableData(it, root))
        })
        return root
    }


    private fun getExchangeRateTableData(
        authorityRate: ExchangeRate,
        root: View
    ) = ExchangeRateTableData(
        authorityRate = authorityRate,
        exchangePointRate = activity!!.intent.extras[CameraResultActivity.EXCHANGE_RATE_RESULT] as ExchangeRate,
        root = root
    )

}