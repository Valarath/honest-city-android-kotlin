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
import cz.city.honest.application.view.component.rate.ExchangeRateTable
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
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val root = inflater.inflate(R.layout.exchange_rate_table, container, false)
        subjectDetailViewModel.authorityRate.observe(viewLifecycleOwner, Observer {
            ExchangeRateTable(activity!!,it,root)
        })
        return root
    }

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