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
import cz.city.honest.application.view.detail.ui.main.SuggestionTableRowConverter
import cz.city.honest.application.view.detail.ui.main.ShowSubjectSuggestionRowDecoratorProvider
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

class UserDetailSuggestionsFragment() : UserDetailFragment(){

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = getRoot(inflater, container)
        userDetailViewModel.userSuggestions.observe(viewLifecycleOwner, Observer { addSuggestions(it, root) })
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
                    decorate(SuggestionTableRowConverter.asTableRow(it, activity),activity!!,it))
            }
        }

    private fun decorate(view: View, context: Context, suggestion:Suggestion) =UserDetailSuggestionRowDecoratorProvider.provide(view.javaClass).decorate(view, context,suggestion)

        private fun getTableLayout(root: View): TableLayout =
        root.findViewById(R.id.suggestions_holder)
}


sealed class UserDetailSuggestionRowDecorator<VIEW_TYPE : View>{
    abstract fun decorate(view: VIEW_TYPE, context: Context, suggestion:Suggestion):VIEW_TYPE


    private fun getButtonLayoutParams() = TableRow.LayoutParams(
        TableRow.LayoutParams.WRAP_CONTENT,
        TableRow.LayoutParams.WRAP_CONTENT
    )

    protected open fun getVoteCount(context: Context, suggestion:Suggestion): TextView =
        TextView(context)
            .apply { text=suggestion.votes.toString() }

    protected open fun getDeleteButton(context: Context, suggestion:Suggestion) =
        Button(context).apply {
            text = "delete"
        }

    protected open fun addDeleteButton(tableRow: TableRow,context: Context, suggestion:Suggestion) =
        tableRow.apply {
            if(suggestion.state == State.IN_PROGRESS)
                addView(getDeleteButton(context, suggestion))
        }
}

class UserDetailSuggestionTableRowDecorator : UserDetailSuggestionRowDecorator<TableRow>() {
    override fun decorate(view: TableRow, context: Context, suggestion:Suggestion) = view.apply {
        addDeleteButton(this,context, suggestion)
    }
}

class UserDetailSuggestionTableLayoutDecorator : UserDetailSuggestionRowDecorator<TableLayout>() {
    override fun decorate(view: TableLayout, context: Context, suggestion:Suggestion) = view.apply {
        children.iterator().forEach {
            decorateTableRow(it, context,suggestion)
        }
    }

    private fun decorateTableRow(it: View, context: Context, suggestion:Suggestion) {
        if (it is TableRow)
            UserDetailSuggestionRowDecoratorProvider.provide(it.javaClass).decorate(it, context,suggestion)
    }
}