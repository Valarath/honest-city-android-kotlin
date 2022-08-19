package cz.city.honest.view.filter

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.lifecycle.ViewModelProvider
import cz.city.honest.view.MapActivity
import cz.city.honest.view.R
import cz.city.honest.view.databinding.FragmentFilterBinding
import cz.city.honest.viewmodel.FilterViewModel
import dagger.android.support.DaggerAppCompatDialogFragment
import javax.inject.Inject

class FilterFragment : DaggerAppCompatDialogFragment() {


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

}