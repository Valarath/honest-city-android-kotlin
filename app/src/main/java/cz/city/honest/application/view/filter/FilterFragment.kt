package cz.city.honest.application.view.filter

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CompoundButton
import android.widget.Switch
import androidx.lifecycle.ViewModelProvider
import cz.city.honest.application.R
import cz.city.honest.application.databinding.FragmentFilterBinding
import cz.city.honest.application.model.dto.HonestyStatus
import cz.city.honest.application.model.dto.SubjectFilter
import cz.city.honest.application.view.MapActivity
import cz.city.honest.application.viewmodel.FilterViewModel
import dagger.android.support.DaggerAppCompatDialogFragment
import javax.inject.Inject

class FilterFragment : DaggerAppCompatDialogFragment(), CompoundButton.OnCheckedChangeListener {


    private lateinit var filterViewModel: FilterViewModel

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View =
        FragmentFilterBinding.inflate(inflater)
            .also { setViewModels() }
            .also { it.lifecycleOwner = viewLifecycleOwner }
            .also { it.filterViewModel =  this.filterViewModel}
            .root
            .also { setOkButton(it) }

    private fun setViewModels() =
        this.apply {
            filterViewModel =
                ViewModelProvider(this, viewModelFactory).get(FilterViewModel::class.java)
        }

    private fun setOkButton(root: View) =
        root.findViewById<Button>(R.id.filter_confirm_button)
            .also { it.setOnClickListener { useFilters() } }

    private fun useFilters() =
        startActivity(Intent(activity, MapActivity::class.java))

    /**private fun setFilter(root: View) =
        filterViewModel.filterData.observeForever { setSubjectFilter(root, it.subjectFilter) }
**/
    private fun setSubjectFilter(root: View, subjectFilter: SubjectFilter) =
        root.also {
            setSwitchButton(it, R.id.show_honest_switch, isSubjectSwitchButtonChecked(HonestyStatus.HONEST,subjectFilter))
            setSwitchButton(it, R.id.show_honest_with_reserve_switch, isSubjectSwitchButtonChecked(HonestyStatus.HONEST_WITH_RESERVE,subjectFilter))
            setSwitchButton(it, R.id.show_be_caution_switch, isSubjectSwitchButtonChecked(HonestyStatus.BE_CAUTION,subjectFilter))
            setSwitchButton(it, R.id.show_dishonest_switch, isSubjectSwitchButtonChecked(HonestyStatus.DISHONEST,subjectFilter))
            setSwitchButton(it, R.id.show_unknown_switch, isSubjectSwitchButtonChecked(HonestyStatus.UNKNOWN,subjectFilter))
        }

    private fun isSubjectSwitchButtonChecked(honestyStatus: HonestyStatus,subjectFilter: SubjectFilter) =
        subjectFilter.honestyStatusVisibilityMap[honestyStatus]!!

    private fun setSwitchButton(root: View, buttonId: Int, isChecked: Boolean) =
        root.findViewById<Switch>(buttonId)
            .also { it.setOnCheckedChangeListener(null) }
            .also { it.isChecked = isChecked }
            //.also { it.setOnCheckedChangeListener(this) }

    override fun onCheckedChanged(
        switch: CompoundButton,
        isChecked: Boolean
    ) =




        requireActivity()
        .getSharedPreferences("HONEST_CITY_PREFERENCES", Context.MODE_PRIVATE).edit()
        .apply { putBoolean(switch.id.toString(), isChecked) }
        .apply()


}