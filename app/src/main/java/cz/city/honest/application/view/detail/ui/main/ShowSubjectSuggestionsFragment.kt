package cz.city.honest.application.view.detail.ui.main

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TableLayout
import android.widget.TableRow
import android.widget.TextView
import androidx.core.view.children
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
                addView(
                    decorate(
                        SuggestionTableRowConverter.asTableRow(it, activity),
                        activity!!
                    )
                )
            }
        }

    private fun decorate(view: View, context: Context) =  ShowSubjectSuggestionRowDecoratorProvider.provide(view.javaClass).decorate(view,context)

    private fun getWatchedSubject(): Long =
        (activity!!.intent.extras[SubjectDetailActivity.INTENT_SUBJECT] as WatchedSubject)
            .id

    private fun getTableLayout(root: View): TableLayout =
        root.findViewById(R.id.suggestions_holder)


}

sealed class  ShowSubjectSuggestionRowDecorator<VIEW_TYPE : View>{
    abstract fun decorate(view: VIEW_TYPE, context: Context):VIEW_TYPE

    protected open fun getVoteButton(context: Context): Button = Button(context).apply{
        layoutParams = getButtonLayoutParams()
        text = resources.getString(R.string.vote_for_suggestion_button)
        setOnClickListener {
            val button = (it as Button)
            if (button.text == resources.getString(R.string.vote_for_suggestion_button))
                voteFor(button)
            else
                unVoteFor(button)
        }
    }

    private fun unVoteFor( button: Button) = button.apply {
        text = resources.getString(R.string.vote_for_suggestion_button)
        button.setCompoundDrawablesWithIntrinsicBounds(0,0,0,0)
    }

    private fun voteFor( button: Button) = button.apply {
        text = null
        button.setCompoundDrawablesWithIntrinsicBounds(R.drawable.checkmark,0,0,0)
    }

    private fun getButtonLayoutParams() = TableRow.LayoutParams(
            TableRow.LayoutParams.WRAP_CONTENT,
            TableRow.LayoutParams.WRAP_CONTENT
        )

}

class ShowSubjectSuggestionTableRowDecorator : ShowSubjectSuggestionRowDecorator<TableRow>() {
    override fun decorate(view: TableRow, context: Context) = view.apply {
        addView(getVoteButton(context))
    }
}

class ShowSubjectSuggestionTableLayoutDecorator : ShowSubjectSuggestionRowDecorator<TableLayout>() {
    override fun decorate(view: TableLayout, context: Context) = view.apply {
        children.iterator().forEach {
            decorateTableRow(it, context)
        }
    }

    private fun decorateTableRow(it: View, context: Context) {
        if (it is TableRow)
            ShowSubjectSuggestionRowDecoratorProvider.provide(it.javaClass).decorate(it, context)
    }
}

