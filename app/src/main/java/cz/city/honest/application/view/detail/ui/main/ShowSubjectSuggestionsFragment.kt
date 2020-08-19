package cz.city.honest.application.view.detail.ui.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TableLayout
import cz.city.honest.application.R
import cz.city.honest.application.model.dto.Suggestion
import cz.city.honest.application.model.service.SuggestionService
import cz.city.honest.application.view.detail.SubjectDetailActivity
import cz.city.honest.mobile.model.dto.WatchedSubject
import dagger.android.support.DaggerAppCompatDialogFragment
import javax.inject.Inject


class ShowSubjectSuggestionsFragment : DaggerAppCompatDialogFragment() {

    @Inject
    lateinit var suggestionService: SuggestionService;

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = getRoot(inflater, container)
        addSuggestions(suggestionService.getSuggestionsForSubject(getWatchedSubject()), root)
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

    private fun getWatchedSubject(): Long =
        (activity!!.intent.extras[SubjectDetailActivity.INTENT_SUBJECT] as WatchedSubject)
            .id

    private fun getTableLayout(root: View): TableLayout =
        root.findViewById(R.id.suggestions_holder)

}