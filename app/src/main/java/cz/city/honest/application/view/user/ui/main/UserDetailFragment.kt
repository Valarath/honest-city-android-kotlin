package cz.city.honest.application.view.user.ui.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TableLayout
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import cz.city.honest.application.R
import cz.city.honest.application.model.dto.Suggestion
import cz.city.honest.application.view.detail.ui.main.SuggestionTableRowConverter
import cz.city.honest.application.viewmodel.UserDetailViewModel
import dagger.android.support.DaggerAppCompatDialogFragment
import javax.inject.Inject

sealed class UserDetailFragment() : DaggerAppCompatDialogFragment() {

    protected lateinit var userDetailViewModel: UserDetailViewModel

    @Inject
    protected lateinit var viewModelFactory: ViewModelProvider.Factory

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        userDetailViewModel =
            ViewModelProvider(this, viewModelFactory).get(UserDetailViewModel::class.java)
    }
}

class UserDetailSettingsFragment() : UserDetailFragment()

class UserDetailSuggestionsFragment() : UserDetailFragment(){

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = getRoot(inflater, container)
        userDetailViewModel.userSuggestions.observe(viewLifecycleOwner, Observer {
        addSuggestions(it, root)
        })
        return root
    }

    private fun getRoot(
        inflater: LayoutInflater,
        container: ViewGroup?
    ) = inflater.inflate(R.layout.fragment_show_subject_suggestions, container, false)


    private fun addSuggestions(suggestions: List<Suggestion>, root: View) =
        getTableLayout(root).apply {
            suggestions.forEach {
                addView(SuggestionTableRowConverter.asTableRow(it, activity))
            }
        }

    private fun getTableLayout(root: View): TableLayout =
        root.findViewById(R.id.suggestions_holder)
}