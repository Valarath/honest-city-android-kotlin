package cz.city.honest.view.user.ui.main

import android.content.Context
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.view.children
import androidx.core.view.size
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import cz.city.honest.view.R
import cz.city.honest.dto.State
import cz.city.honest.dto.Suggestion
import cz.city.honest.dto.UserSuggestion
import cz.city.honest.dto.UserSuggestionStateMarking
import cz.city.honest.view.component.suggestion.SuggestionTableRowConverter
import cz.city.honest.view.detail.ui.main.TableRowCreator
import cz.city.honest.viewmodel.UserDetailViewModel
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

class UserDetailSuggestionsFragment() : UserDetailFragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = getRoot(inflater, container)
        userDetailViewModel.userSuggestions.observe(viewLifecycleOwner, Observer {
            addSuggestions(it, root, getHeaders(root))
        })
        return root
    }

    private fun getRoot(
        inflater: LayoutInflater,
        container: ViewGroup?
    ) = inflater.inflate(R.layout.fragment_user_detail, container, false)


    private fun addSuggestions(suggestions: List<UserSuggestion>, root: View, header: TableRow) =
        getTableLayout(root)
            .apply { removeAllViews() }
            .apply { addHeaderToSuggestionsHolder(header) }
            .apply {
                suggestions.forEach {
                    addView(
                        decorate(
                            SuggestionTableRowConverter.asTableRow(it, activity),
                            activity!!,
                            it
                        )
                    )
                }
            }

    private fun TableLayout.addHeaderToSuggestionsHolder(header: TableRow) {
        this.addView(header)
    }

    private fun getHeaders(root: View): TableRow =
        root.findViewById(R.id.suggestions_holder_header)

    private fun decorate(view: View, context: Context, suggestion: UserSuggestion) =
        UserDetailSuggestionRowDecoratorProvider.provide(view.javaClass)
            .decorate(
                view,
                UserDetailSuggestionRowDecoratorData(context, suggestion, userDetailViewModel)
            )

    private fun getTableLayout(root: View): TableLayout =
        root.findViewById(R.id.user_detail_suggestions_holder)
}


sealed class UserDetailSuggestionRowDecorator<VIEW_TYPE : View> {
    abstract fun decorate(
        view: VIEW_TYPE,
        userDetailSuggestionRowDecoratorData: UserDetailSuggestionRowDecoratorData
    ): VIEW_TYPE


    private fun getButtonLayoutParams() = TableRow.LayoutParams(
        TableRow.LayoutParams.WRAP_CONTENT,
        TableRow.LayoutParams.WRAP_CONTENT
    )

    protected open fun getVoteCount(context: Context, suggestion: Suggestion): TextView =
        TextView(context)
            .apply { text = suggestion.votes.toString() }

    protected open fun getDeleteButton(
        userDetailSuggestionRowDecoratorData: UserDetailSuggestionRowDecoratorData
    ) =
        Button(userDetailSuggestionRowDecoratorData.context).apply {
            setDeleteButtonCell(userDetailSuggestionRowDecoratorData)
            text = resources.getString(R.string.delete_user_suggestion)
            setOnClickListener {
                this.visibility = View.INVISIBLE
                userDetailSuggestionRowDecoratorData.viewModel.deleteSuggestion(
                    userDetailSuggestionRowDecoratorData.userSuggestion
                )
            }
        }

    private fun Button.setDeleteButtonCell(
        userDetailSuggestionRowDecoratorData: UserDetailSuggestionRowDecoratorData
    ) {
        this.setBackgroundColor(userDetailSuggestionRowDecoratorData.context.getColor(R.color.suggestionDeclined))
        this.gravity = Gravity.CENTER
        this.textSize = 20f
        this.setTextColor(userDetailSuggestionRowDecoratorData.context.getColor(R.color.white))
        this.layoutParams = TableRowCreator.getTableCellLayoutsParams(10f, 5, 50)
    }

    protected open fun addDeleteButton(
        tableRow: TableRow,
        userDetailSuggestionRowDecoratorData: UserDetailSuggestionRowDecoratorData
    ) = tableRow.apply {
        if (isPossibleToDeleteSuggestion(userDetailSuggestionRowDecoratorData))
            performAddDeleteButton(userDetailSuggestionRowDecoratorData, tableRow)
    }

    private fun performAddDeleteButton(
        userDetailSuggestionRowDecoratorData: UserDetailSuggestionRowDecoratorData,
        tableRow: TableRow
    ) = tableRow
        .apply {
            this.removeViewAt(this.size - 1)
            this.addView(getDeleteButton(userDetailSuggestionRowDecoratorData))
        }

    private fun isPossibleToDeleteSuggestion(userDetailSuggestionRowDecoratorData: UserDetailSuggestionRowDecoratorData) =
        userDetailSuggestionRowDecoratorData.userSuggestion.suggestion.state == State.IN_PROGRESS
                && !userDetailSuggestionRowDecoratorData.userSuggestion.metadata.processed
                && userDetailSuggestionRowDecoratorData.userSuggestion.metadata.markAs != UserSuggestionStateMarking.DELETE
}

class UserDetailSuggestionTableRowDecorator : UserDetailSuggestionRowDecorator<TableRow>() {
    override fun decorate(
        view: TableRow,
        userDetailSuggestionRowDecoratorData: UserDetailSuggestionRowDecoratorData
    ) = view.apply {
        addDeleteButton(this, userDetailSuggestionRowDecoratorData)
    }
}

class UserDetailSuggestionTableLayoutDecorator : UserDetailSuggestionRowDecorator<TableLayout>() {
    override fun decorate(
        view: TableLayout,
        userDetailSuggestionRowDecoratorData: UserDetailSuggestionRowDecoratorData
    ) = view.apply {
        children.iterator().forEach {
            decorateTableRow(it, userDetailSuggestionRowDecoratorData)
        }
    }

    private fun decorateTableRow(
        it: View,
        userDetailSuggestionRowDecoratorData: UserDetailSuggestionRowDecoratorData
    ) {
        if (it is TableRow)
            UserDetailSuggestionRowDecoratorProvider.provide(it.javaClass)
                .decorate(it, userDetailSuggestionRowDecoratorData)
    }
}

data class UserDetailSuggestionRowDecoratorData(
    val context: Context,
    val userSuggestion: UserSuggestion,
    val viewModel: UserDetailViewModel
)