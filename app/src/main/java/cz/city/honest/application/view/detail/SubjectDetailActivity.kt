package cz.city.honest.application.view.detail

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.lifecycle.ViewModelProvider
import androidx.viewpager.widget.ViewPager
import com.google.android.material.tabs.TabLayout
import cz.city.honest.application.R
import cz.city.honest.application.model.dto.ClosedExchangePointSuggestion
import cz.city.honest.application.model.dto.State
import cz.city.honest.application.model.dto.UserSuggestionStateMarking
import cz.city.honest.application.model.dto.WatchedSubject
import cz.city.honest.application.view.camera.CameraActivity
import cz.city.honest.application.view.detail.ui.main.ShowSubjectSuggestionsViewModel
import cz.city.honest.application.view.detail.ui.main.SubjectPagerAdapter
import cz.city.honest.application.viewmodel.converter.NewExchangePointSuggestionExchangePointConverter
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
        setSuggestDifferentRateButton(menu)
        return true
    }

    private fun setReportClosedSubjectButton(menu: Menu) {
        if (isNewSubjectSuggestion() || isCloseSubjectSuggestionSuggested() || showSubjectSuggestionsViewModel.loggedUser == null)
            menu.findItem(R.id.suggest_non_existing_subject)
                .apply { disableMenuItem(this) }
    }

    private fun setSuggestDifferentRateButton(menu: Menu) =
        menu.findItem(R.id.suggest_different_rate)
            .apply {
                if (showSubjectSuggestionsViewModel.loggedUser == null && !isNewSubjectSuggestion())
                    disableMenuItem(this)
            }
            .apply {
                if (isNewSubjectSuggestion())
                    this.title = R.string.analyze_actual_rate.toString()
            }

    private fun disableMenuItem(menuItem: MenuItem) = menuItem.apply {
        this.isEnabled = false
        this.isVisible = false
    }

    private fun isNewSubjectSuggestion() =
        getWatchedSubjectId() == NewExchangePointSuggestionExchangePointConverter.getId()

    private fun isCloseSubjectSuggestionSuggested() =
        showSubjectSuggestionsViewModel.getSuggestionsForSubject(getWatchedSubjectId())
            .any { it.suggestion is ClosedExchangePointSuggestion }

    private fun getWatchedSubjectId() =
        getWatchedSubject().id

    private fun getWatchedSubject() = (intent.extras[WATCHED_SUBJECT] as WatchedSubject)

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

    private fun suggestExchangeRateChange() =
        Intent(this, CameraActivity::class.java)
            .apply { this.putExtra(CameraActivity.WATCHED_SUBJECT, getWatchedSubject()) }
            .let { this.startActivity(it) }
            .let { true }


    companion object {
        const val WATCHED_SUBJECT: String = "watchedSubject"
    }
}

