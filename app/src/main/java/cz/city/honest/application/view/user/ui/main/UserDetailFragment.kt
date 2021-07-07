package cz.city.honest.application.view.user.ui.main

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
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import cz.city.honest.application.R
import cz.city.honest.application.model.dto.State
import cz.city.honest.application.model.dto.Suggestion
import cz.city.honest.application.model.dto.UserSuggestion
import cz.city.honest.application.model.dto.UserSuggestionStateMarking
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

class UserDetailSuggestionsFragment() : UserDetailFragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = getRoot(inflater, container)
        userDetailViewModel.userSuggestions.observe(viewLifecycleOwner,Observer{
            addSuggestions(it,root)
        })
        return root
    }

    private fun getRoot(
        inflater: LayoutInflater,
        container: ViewGroup?
    ) = inflater.inflate(R.layout.fragment_show_subject_suggestions, container, false)


    private fun addSuggestions(suggestions: List<UserSuggestion>, root: View) =
        getTableLayout(root).apply {
            suggestions.forEach {
                addView(
                    decorate(SuggestionTableRowConverter.asTableRow(it, activity), activity!!, it)
                )
            }
        }

    private fun decorate(view: View, context: Context, suggestion: UserSuggestion) =
        UserDetailSuggestionRowDecoratorProvider.provide(view.javaClass)
            .decorate(
                view,
                UserDetailSuggestionRowDecoratorData(context, suggestion, userDetailViewModel)
            )

    private fun getTableLayout(root: View): TableLayout =
        root.findViewById(R.id.suggestions_holder)
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

    protected open fun getDeleteButton(userDetailSuggestionRowDecoratorData: UserDetailSuggestionRowDecoratorData) =
        Button(userDetailSuggestionRowDecoratorData.context).apply {
            text = resources.getString(R.string.delete_user_suggestion)
            setOnClickListener {
                userDetailSuggestionRowDecoratorData.viewModel.deleteSuggestion(userDetailSuggestionRowDecoratorData.userSuggestion)
            }
        }

    protected open fun addDeleteButton(
        tableRow: TableRow,
        userDetailSuggestionRowDecoratorData: UserDetailSuggestionRowDecoratorData
    ) = tableRow.apply {
            if (isPossibleToDeleteSuggestion(userDetailSuggestionRowDecoratorData))
                addView(getDeleteButton(userDetailSuggestionRowDecoratorData))
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