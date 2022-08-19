package cz.city.honest.view.detail.ui.main

import android.content.Context
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TableLayout
import android.widget.TableRow
import androidx.activity.ComponentActivity
import androidx.core.view.children
import androidx.core.view.size
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import cz.city.honest.view.R
import cz.city.honest.dto.State
import cz.city.honest.dto.WatchedSubject
import cz.city.honest.view.component.suggestion.SuggestionTableRowConverter
import cz.city.honest.view.detail.SubjectDetailActivity
import cz.city.honest.viewmodel.ShowSubjectSuggestionsViewModel
import cz.city.honest.viewmodel.VotedSuggestion
import dagger.android.support.DaggerAppCompatDialogFragment
import javax.inject.Inject


class ShowSubjectSuggestionsFragment : DaggerAppCompatDialogFragment() {


    protected lateinit var showSubjectSuggestionsViewModel: ShowSubjectSuggestionsViewModel;

    @Inject
    protected lateinit var viewModelFactory: ViewModelProvider.Factory

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        showSubjectSuggestionsViewModel =
            ViewModelProvider(
                this,
                viewModelFactory
            ).get(ShowSubjectSuggestionsViewModel::class.java)
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = getRoot(inflater, container)
        showSubjectSuggestionsViewModel.subjectId.postValue(getWatchedSubjectId())
        addSuggestions(root)
        return root
    }

    private fun getRoot(
        inflater: LayoutInflater,
        container: ViewGroup?
    ) = inflater.inflate(R.layout.fragment_show_subject_suggestions, container, false)

    private fun addSuggestions(root: View) =
        getTableLayout(root).also { tableLayout ->
            showSubjectSuggestionsViewModel.subjectSuggestions.observe(
                viewLifecycleOwner,
                androidx.lifecycle.Observer {
                    addSuggestions(it, tableLayout, getHeaders(root))
                })
        }

    private fun addSuggestions(
        votedSuggestions: List<VotedSuggestion>,
        tableLayout: TableLayout,
        tableHeader: TableRow
    ) = tableLayout.also { it.removeAllViews() }
            .also { it.addView(tableHeader) }
            .also {
                votedSuggestions.forEach {
                    tableLayout.addView(
                        decorate(
                            SuggestionTableRowConverter.asTableRow(it, activity),
                            activity!!,
                            it
                        )
                    )
                }
            }

    private fun getHeaders(root: View): TableRow =
        root.findViewById(R.id.suggestions_holder_header)

    private fun decorate(view: View, context: ComponentActivity, suggestion: VotedSuggestion) =
        ShowSubjectSuggestionRowDecoratorProvider.provide(view.javaClass)
            .decorate(
                view,
                ShowSubjectSuggestionRowDecoratorData(
                    this,
                    context,
                    suggestion,
                    showSubjectSuggestionsViewModel,
                    getWatchedSubjectId()
                )
            )

    private fun getWatchedSubjectId(): String =
        (activity!!.intent.extras!![SubjectDetailActivity.WATCHED_SUBJECT] as WatchedSubject).id

    private fun getTableLayout(root: View): TableLayout =
        root.findViewById(R.id.suggestions_holder)


}

sealed class ShowSubjectSuggestionRowDecorator<VIEW_TYPE : View> {
    abstract fun decorate(view: VIEW_TYPE, data: ShowSubjectSuggestionRowDecoratorData): VIEW_TYPE

    protected open fun getVoteButton(data: ShowSubjectSuggestionRowDecoratorData): Button =
        Button(data.context).apply {
            this.setBackgroundColor(this.context.getColor(R.color.suggestionAccepted))
            this.setTextColor(this.context.getColor(R.color.white))
            this.gravity = Gravity.CENTER
            this.textSize = 12f
            setButtonText(this, data)
            layoutParams = TableRowCreator.getTableCellLayoutsParams(10f, 5, 50)
            setOnClickListener { setVoteButtonOnClickListener(it, data) }
        }

    private fun Button.setVoteButtonOnClickListener(
        it: View,
        data: ShowSubjectSuggestionRowDecoratorData
    ) = (it as Button).also {
        if (it.text == resources.getString(R.string.vote_for_suggestion_button))
            voteFor(it, data)
    }


    private fun setButtonText(button: Button, data: ShowSubjectSuggestionRowDecoratorData) =
        button.apply {
            if (data.votedSuggestion.voted)
                button.also { it.isEnabled = false }
                    .setCompoundDrawablesWithIntrinsicBounds(R.drawable.checkmark, 0, 0, 0)
            else
                text = resources.getString(R.string.vote_for_suggestion_button)
        }

    private fun voteFor(button: Button, data: ShowSubjectSuggestionRowDecoratorData) =
        button.apply {
            button.isEnabled = false
            text = null
            data.viewModel.voteFor(data.votedSuggestion.suggestion)
            button.setCompoundDrawablesWithIntrinsicBounds(R.drawable.checkmark, 0, 0, 0)
        }

}

class ShowSubjectSuggestionTableRowDecorator : ShowSubjectSuggestionRowDecorator<TableRow>() {
    override fun decorate(view: TableRow, data: ShowSubjectSuggestionRowDecoratorData) =
        view.apply {
            data.viewModel.loggedUser.observe(data.fragment, Observer {
                if (data.votedSuggestion.suggestion.state == State.IN_PROGRESS)
                    addVoteButton(data)
            })


        }

    private fun TableRow.addVoteButton(data: ShowSubjectSuggestionRowDecoratorData) {
        this.removeViewAt(this.size - 1)
        this.addView(getVoteButton(data))
    }


}

class ShowSubjectSuggestionTableLayoutDecorator : ShowSubjectSuggestionRowDecorator<TableLayout>() {
    override fun decorate(view: TableLayout, data: ShowSubjectSuggestionRowDecoratorData) =
        view.apply {
            children.iterator().forEach {
                decorateTableRow(it, data)
            }
        }

    private fun decorateTableRow(view: View, data: ShowSubjectSuggestionRowDecoratorData) {
        if (view is TableRow)
            ShowSubjectSuggestionRowDecoratorProvider.provide(view.javaClass)
                .decorate(view, data)
    }
}

data class ShowSubjectSuggestionRowDecoratorData(
    val fragment: DaggerAppCompatDialogFragment,
    val context: Context,
    val votedSuggestion: VotedSuggestion,
    val viewModel: ShowSubjectSuggestionsViewModel,
    val watchedSubjectId: String
)
