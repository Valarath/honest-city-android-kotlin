package cz.city.honest.application.view.detail.ui.main

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TableLayout
import android.widget.TableRow
import androidx.core.view.children
import androidx.lifecycle.ViewModelProvider
import cz.city.honest.application.R
import cz.city.honest.application.model.dto.State
import cz.city.honest.application.model.dto.WatchedSubject
import cz.city.honest.application.view.detail.SubjectDetailActivity
import dagger.android.support.DaggerAppCompatDialogFragment
import javax.inject.Inject


class ShowSubjectSuggestionsFragment : DaggerAppCompatDialogFragment() {


    protected lateinit var showSubjectSuggestionsViewModel:ShowSubjectSuggestionsViewModel;

    @Inject
    protected lateinit var viewModelFactory: ViewModelProvider.Factory

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        showSubjectSuggestionsViewModel =
            ViewModelProvider(this, viewModelFactory).get(ShowSubjectSuggestionsViewModel::class.java)
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = getRoot(inflater, container)
        addSuggestions(root, getWatchedSubjectId())
        return root
    }

    private fun getRoot(
        inflater: LayoutInflater,
        container: ViewGroup?
    ) = inflater.inflate(R.layout.fragment_show_subject_suggestions, container, false)

    private fun addSuggestions(root: View, watchedSubjectId: String) =
        getTableLayout(root).apply {
            showSubjectSuggestionsViewModel.getSuggestionsForSubject(watchedSubjectId).forEach {
                addView(
                    decorate(
                        SuggestionTableRowConverter.asTableRow(it, activity),
                        activity!!,
                        it
                    )
                )
            }
        }

    private fun decorate(view: View, context: Context, suggestion: VotedSuggestion) =
        ShowSubjectSuggestionRowDecoratorProvider.provide(view.javaClass)
            .decorate(view, ShowSubjectSuggestionRowDecoratorData(context,suggestion,showSubjectSuggestionsViewModel,getWatchedSubjectId()))

    private fun getWatchedSubjectId(): String =
        (activity!!.intent.extras[SubjectDetailActivity.WATCHED_SUBJECT] as WatchedSubject).id

    private fun getTableLayout(root: View): TableLayout =
        root.findViewById(R.id.suggestions_holder)


}

sealed class ShowSubjectSuggestionRowDecorator<VIEW_TYPE : View> {
    abstract fun decorate(view: VIEW_TYPE, data:ShowSubjectSuggestionRowDecoratorData): VIEW_TYPE

    protected open fun getVoteButton(data:ShowSubjectSuggestionRowDecoratorData): Button = Button(data.context).apply {
        layoutParams = getButtonLayoutParams()
        setButtonText(this,data)
        setOnClickListener {
            val button = (it as Button)
            if (button.text == resources.getString(R.string.vote_for_suggestion_button))
                voteFor(button,data)
        }
    }

    private fun setButtonText(button: Button, data:ShowSubjectSuggestionRowDecoratorData) =
        button.apply {
            if(data.votedSuggestion.voted)
                button.setCompoundDrawablesWithIntrinsicBounds(R.drawable.checkmark, 0, 0, 0)
            else
                text = resources.getString(R.string.vote_for_suggestion_button)
        }

    private fun voteFor(button: Button, data:ShowSubjectSuggestionRowDecoratorData) = button.apply {
        text = null
        data.viewModel.voteFor(data.votedSuggestion.suggestion,data.watchedSubjectId)
        button.setCompoundDrawablesWithIntrinsicBounds(R.drawable.checkmark, 0, 0, 0)
    }

    private fun getButtonLayoutParams() = TableRow.LayoutParams(
        TableRow.LayoutParams.WRAP_CONTENT,
        TableRow.LayoutParams.WRAP_CONTENT
    )

}

class ShowSubjectSuggestionTableRowDecorator : ShowSubjectSuggestionRowDecorator<TableRow>() {
    override fun decorate(view: TableRow, data:ShowSubjectSuggestionRowDecoratorData) = view.apply {
        if (data.votedSuggestion.suggestion.state == State.IN_PROGRESS)
            addView(getVoteButton(data))
    }
}

class ShowSubjectSuggestionTableLayoutDecorator : ShowSubjectSuggestionRowDecorator<TableLayout>() {
    override fun decorate(view: TableLayout, data:ShowSubjectSuggestionRowDecoratorData) =
        view.apply {
            children.iterator().forEach {
                decorateTableRow(it, data)
            }
        }

    private fun decorateTableRow(view: View, data:ShowSubjectSuggestionRowDecoratorData) {
        if (view is TableRow)
            ShowSubjectSuggestionRowDecoratorProvider.provide(view.javaClass)
                .decorate(view, data)
    }
}

data class ShowSubjectSuggestionRowDecoratorData(val context: Context, val votedSuggestion: VotedSuggestion, val viewModel:ShowSubjectSuggestionsViewModel, val watchedSubjectId:String)
