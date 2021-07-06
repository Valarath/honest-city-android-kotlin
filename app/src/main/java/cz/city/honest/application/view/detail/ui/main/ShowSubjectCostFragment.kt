package cz.city.honest.application.view.detail.ui.main

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import cz.city.honest.application.R
import cz.city.honest.application.model.dto.*
import cz.city.honest.application.view.camera.CameraActivity
import cz.city.honest.application.view.component.rate.ExchangeRateTable
import cz.city.honest.application.view.component.rate.ExchangeRateTableData
import cz.city.honest.application.view.detail.SubjectDetailActivity
import cz.city.honest.application.viewmodel.SubjectDetailViewModel
import cz.city.honest.application.viewmodel.converter.NewExchangePointSuggestionExchangePointConverter
import dagger.android.support.DaggerAppCompatDialogFragment
import javax.inject.Inject


class ShowSubjectCostFragment : DaggerAppCompatDialogFragment() {

    private lateinit var subjectDetailViewModel: SubjectDetailViewModel

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        subjectDetailViewModel =
            ViewModelProvider(this, viewModelFactory).get(SubjectDetailViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val root = inflater.inflate(R.layout.fragment_subject_detail, container, false)
        initExchangeRateTable(root)
        setSuggestNewRateButton(root)
        return root
    }

    private fun initExchangeRateTable(root: View) {
        subjectDetailViewModel.authorityRate.observe(viewLifecycleOwner, Observer {
            setComponentsVisibility(root, it)
        })
    }

    private fun findExchangeRateTable(root: View) =
        root.findViewById<ExchangeRateTable>(R.id.exchange_rate)

    private fun setComponentsVisibility(
        root: View,
        it: ExchangeRate
    ) {
        val subjectRate = getExchangePointRate()
        if (subjectRate == null || subjectRate.rates.isEmpty())
            showSuggestRateButtonOnly(root)
        else
            showExchangeRateTableOnly(root, it, subjectRate)
    }

    private fun showSuggestRateButtonOnly(
        root: View
    ) {
        getSuggestRateButton(root)
            .apply { this.visibility = View.VISIBLE }
        findExchangeRateTable(root).visibility = View.GONE
    }

    private fun showExchangeRateTableOnly(
        root: View,
        it: ExchangeRate,
        subjectRate: ExchangeRate
    ) {
        val exchangeRateTable = findExchangeRateTable(root)
        setSuggestNewRateButton(root)
        exchangeRateTable.visibility = View.VISIBLE
        exchangeRateTable.showExchangePointRates(getExchangeRateTableData(it, subjectRate, root))
    }

    private fun setSuggestNewRateButton(root: View) =
        getSuggestRateButton(root)
            .apply { setButtonText() }
            .apply { this.visibility = View.GONE }
            .apply { this.setOnClickListener { suggestExchangeRateChange() } }

    private fun Button.setButtonText() =
        if (getExchangePoint().id == NewExchangePointSuggestionExchangePointConverter.getId() || subjectDetailViewModel.loggedUser == null)
            this.text = getString(R.string.analyze_actual_rate)
        else
            this.text = getString(R.string.suggest_rate_button)

    private fun suggestExchangeRateChange() =
        Intent(activity, CameraActivity::class.java)
            .apply { this.putExtra(CameraActivity.WATCHED_SUBJECT, getExchangePoint()) }
            .let { this.startActivity(it) }

    private fun getSuggestRateButton(root: View) =
        root.findViewById<Button>(R.id.suggest_rate_button)

    private fun getExchangeRateTableData(
        authorityRate: ExchangeRate,
        subjectRate: ExchangeRate,
        root: View
    ) =
        ExchangeRateTableData(authorityRate,subjectRate, root)

    private fun getExchangePointRate() =
        getExchangePoint()
            .exchangePointRate

    private fun getExchangePoint() =
        ((context as Activity)!!.intent.extras[SubjectDetailActivity.WATCHED_SUBJECT] as ExchangePoint)

    companion object {

        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private const val ARG_SECTION_NUMBER = "section_number"

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        @JvmStatic
        fun newInstance(sectionNumber: Int): ShowSubjectCostFragment {
            return ShowSubjectCostFragment().apply {
                arguments = Bundle().apply {
                    putInt(ARG_SECTION_NUMBER, sectionNumber)
                }
            }
        }
    }
}

fun Set<Rate>.toMap(): Map<String, ExchangeRateValues> =
    this.map { it.currency to it.rateValues }.toMap()