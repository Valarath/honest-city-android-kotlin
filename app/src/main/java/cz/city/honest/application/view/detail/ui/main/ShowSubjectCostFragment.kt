package cz.city.honest.application.view.detail.ui.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TableLayout
import android.widget.TableRow
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import cz.city.honest.application.R
import cz.city.honest.application.view.detail.SubjectDetailActivity
import cz.city.honest.application.viewmodel.SubjectDetailViewModel
import cz.city.honest.mobile.model.dto.ExchangePoint
import cz.city.honest.mobile.model.dto.ExchangeRate
import cz.city.honest.mobile.model.dto.ExchangeRateValues
import cz.city.honest.mobile.model.dto.Rate
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
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val root = inflater.inflate(R.layout.fragment_subject_detail, container, false)
        subjectDetailViewModel.authorityRate.observe(viewLifecycleOwner, Observer {
            showExchangePointRates(it, root)
        })
        return root
    }

    private fun showExchangePointRates(
        it: ExchangeRate,
        root: View
    ) {
        val tableLayout = getTableLayout(root)
        val firstRow = tableLayout.findViewById<TableRow>(R.id.exchange_rate_holder_headers)
        tableLayout.removeAllViews()
        tableLayout.addView(firstRow)
        addRows(getRows(it.rates.toMap(), getExchangePointRate()), tableLayout)
    }

    private fun getExchangePointRate() =
        (activity!!.intent.extras[SubjectDetailActivity.INTENT_SUBJECT] as ExchangePoint)
            .exchangePointRate
            .rates
            .toMap()

    private fun getTableLayout(root: View): TableLayout =
        root.findViewById(R.id.exchange_rate_holder)

    private fun addRows(
        tableRows: List<TableRow>,
        tableLayout: TableLayout
    ): Unit = tableRows
        .forEach { tableLayout.addView(it) }

    private fun getRows(
        authorityRates: Map<String, ExchangeRateValues>,
        exchangePointRates: Map<String, ExchangeRateValues>
    ): List<TableRow> =
        authorityRates.entries
            .filter { exchangePointRates[it.key] != null }
            .map { getRow(it.value, exchangePointRates[it.key]!!, it.key) }

    private fun getRow(
        authorityRate: ExchangeRateValues,
        exchangePointRate: ExchangeRateValues,
        currency: String
    ): TableRow =
        TableRow(activity)
            .apply {
                this.addView(TableRowCreator.getCell(currency, activity!!))
                this.addView(TableRowCreator.getCell(exchangePointRate.buy, activity!!))
                this.addView(TableRowCreator.getCell(authorityRate.buy, activity!!))
                this.addView(
                    TableRowCreator.getCell(
                        getPercentageDifferenceFromAuthorityRate(
                            authorityRate,
                            exchangePointRate
                        ), activity!!
                    )
                )
            }

    private fun getPercentageDifferenceFromAuthorityRate(
        authorityRate: ExchangeRateValues,
        exchangePointRate: ExchangeRateValues
    ) = (authorityRate.buy - exchangePointRate.buy) / authorityRate.buy

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