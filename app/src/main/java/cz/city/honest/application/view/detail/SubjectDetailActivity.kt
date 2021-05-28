package cz.city.honest.application.view.detail

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.lifecycle.ViewModelProvider
import androidx.viewpager.widget.ViewPager
import com.google.android.material.tabs.TabLayout
import cz.city.honest.application.R
import cz.city.honest.application.model.dto.ClosedExchangePointSuggestion
import cz.city.honest.application.model.dto.NewExchangePointSuggestion
import cz.city.honest.application.model.dto.State
import cz.city.honest.application.model.dto.UserSuggestionStateMarking
import cz.city.honest.application.view.detail.ui.main.ShowSubjectSuggestionsViewModel
import cz.city.honest.application.view.detail.ui.main.SubjectPagerAdapter
import cz.city.honest.application.viewmodel.converter.NewExchangePointSuggestionExchangePointConverter
import cz.city.honest.mobile.model.dto.WatchedSubject
import dagger.android.support.DaggerAppCompatActivity
import java.util.*
import javax.inject.Inject


class SubjectDetailActivity : DaggerAppCompatActivity() {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    private val MENU_ACTIONS: Map<Int, () -> Boolean> = mapOf(
        R.id.suggest_non_existing_subject to ::suggestNonExistingSubject,
        R.id.suggest_different_rate to ::suggestExchangeRateChange
    )

    private lateinit var showSubjectSuggestionsViewModel: ShowSubjectSuggestionsViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_subject_detail)
        val sectionsPagerAdapter = SubjectPagerAdapter(this, supportFragmentManager)
        val viewPager: ViewPager = findViewById(R.id.view_pager)
        viewPager.adapter = sectionsPagerAdapter
        val tabs: TabLayout = findViewById(R.id.tabs)
        tabs.setupWithViewPager(viewPager)
        showSubjectSuggestionsViewModel =
            ViewModelProvider(
                this,
                viewModelFactory
            ).get(ShowSubjectSuggestionsViewModel::class.java)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.subject_menu, menu)
        setReportClosedSubjectButton(menu)
        return true
    }

    private fun setReportClosedSubjectButton(menu: Menu) {
        if (isNewSubjectSuggestion() || isCloseSubjectSuggestionSuggested())
            menu.findItem(R.id.suggest_non_existing_subject).isEnabled = false
    }

    private fun isNewSubjectSuggestion() =
        getWatchedSubjectId() == NewExchangePointSuggestionExchangePointConverter.getId()

    private fun isCloseSubjectSuggestionSuggested() =
        showSubjectSuggestionsViewModel.getSuggestionsForSubject(getWatchedSubjectId())
            .any { it.suggestion is ClosedExchangePointSuggestion }

    private fun getWatchedSubjectId() =
        (intent.extras[INTENT_SUBJECT] as WatchedSubject).id

    override fun onOptionsItemSelected(item: MenuItem): Boolean =
        if (item.itemId == android.R.id.home)
            finish().run { true }
        else
            MENU_ACTIONS[item.itemId]!!.invoke()

    private fun suggestNonExistingSubject() = showSubjectSuggestionsViewModel
        .suggest(createClosedExchangePointSuggestion(), UserSuggestionStateMarking.DELETE)
        .let { true }

    private fun createClosedExchangePointSuggestion() = ClosedExchangePointSuggestion(
        id = UUID.randomUUID().toString(),
        state = State.IN_PROGRESS,
        votes = 1,
        watchedSubjectId = getWatchedSubjectId()
    )

    private fun suggestExchangeRateChange() = true

    companion object {
        const val INTENT_SUBJECT: String = "intentSubject"
    }
}

