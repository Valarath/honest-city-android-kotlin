package cz.city.honest.application.view.detail.ui.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TableLayout
import android.widget.TableRow
import cz.city.honest.application.R
import cz.city.honest.application.model.dto.ClosedExchangePointSuggestion
import cz.city.honest.application.model.dto.ExchangeRateSuggestion
import cz.city.honest.application.model.dto.NewExchangePointSuggestion
import cz.city.honest.application.model.dto.Suggestion
import cz.city.honest.application.model.service.SuggestionService
import cz.city.honest.application.view.detail.SubjectDetailActivity
import cz.city.honest.mobile.model.dto.WatchedSubject
import javax.inject.Inject


class ShowSubjectSuggestionsFragment : TableRowFragment() {

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
                addView(toTableRow(it))
            }
        }

    private fun toTableRow(suggestion: Suggestion): TableRow =
        when (suggestion) {
            is ExchangeRateSuggestion -> toExchangeRateSuggestionTableRow(suggestion)
            is ClosedExchangePointSuggestion -> toClosedExchangePointSuggestionTableRow(suggestion)
            is NewExchangePointSuggestion -> toNewExchangePointSuggestionTableRow(suggestion)
            else -> throw RuntimeException()
        }

    private fun getWatchedSubject(): Long =
        (activity!!.intent.extras[SubjectDetailActivity.INTENT_SUBJECT] as WatchedSubject)
            .id

    //TODO to handler
    private fun toClosedExchangePointSuggestionTableRow(suggestion: ClosedExchangePointSuggestion): TableRow =
        TableRow(activity).apply {
            addView(getCell(suggestion.state.name, 2f))
            addView(getCell(suggestion.votes, 1f))
        }

    private fun toNewExchangePointSuggestionTableRow(suggestion: NewExchangePointSuggestion): TableRow =
        TableRow(activity).apply {
            addView(getCell(suggestion.state.name, 2f))
            addView(getCell(suggestion.votes, 1f))
        }

    private fun toExchangeRateSuggestionTableRow(suggestion: ExchangeRateSuggestion): TableRow =
        TableRow(activity).apply {
            addView(getCell(suggestion.state.name, 2f))
            addView(getCell(suggestion.votes, 1f))
        }

    private fun getTableLayout(root: View): TableLayout =
        root.findViewById(R.id.suggestions_holder)

}