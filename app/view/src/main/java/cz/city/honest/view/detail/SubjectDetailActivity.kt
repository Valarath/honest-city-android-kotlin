package cz.city.honest.view.detail

import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.util.Base64
import android.view.Menu
import android.view.MenuItem
import androidx.lifecycle.ViewModelProvider
import androidx.viewpager.widget.ViewPager
import com.google.android.material.tabs.TabLayout
import cz.city.honest.dto.*
import cz.city.honest.view.R
import cz.city.honest.view.camera.rate.RateCameraActivity
import cz.city.honest.viewmodel.ShowSubjectSuggestionsViewModel
import cz.city.honest.view.detail.ui.main.SubjectPagerAdapter
import cz.city.honest.viewmodel.VotedSuggestion
import cz.city.honest.viewmodel.converter.NewExchangePointSuggestionExchangePointConverter
import dagger.android.support.DaggerAppCompatActivity
import java.time.Instant
import java.util.*
import javax.inject.Inject


class SubjectDetailActivity : DaggerAppCompatActivity() {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory
    var inflante = true

    private val MENU_ACTIONS: Map<Int, (menuItem: MenuItem) -> Boolean> = mapOf(
        R.id.suggest_non_existing_subject to ::suggestNonExistingSubject,
        R.id.suggest_different_rate to ::suggestExchangeRateChange,
        R.id.vote_for_new_subject_suggestion to ::voteFor
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
        showSubjectSuggestionsViewModel.subjectId.postValue(getWatchedSubjectId())
        setNewSuggestionId()
        setActionBar()
    }

    private fun setNewSuggestionId() = getWatchedSubject().suggestions
        .filterIsInstance<NewExchangePointSuggestion>()
        .forEach { showSubjectSuggestionsViewModel.newSubjectSuggestionId.postValue(it.id) }

    private fun setActionBar() = supportActionBar
        ?.also { setBackgroundImage(it) }

    private fun setBackgroundImage(actionBar: androidx.appcompat.app.ActionBar) =
        Base64.decode(getWatchedSubjectImage(), Base64.DEFAULT)
            .let { BitmapDrawable(resources, BitmapFactory.decodeByteArray(it, 0, it.size)) }
            .also { actionBar.setBackgroundDrawable(it) }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        showSubjectSuggestionsViewModel.loggedUser.observe(this, androidx.lifecycle.Observer {
            if (inflante) {
                menuInflater.inflate(R.menu.subject_menu, menu)
                inflante = false
            }
            showSubjectSuggestionsViewModel.subjectSuggestions.observe(
                this,
                androidx.lifecycle.Observer {
                    setReportClosedSubjectButton(menu, it)
                })
            setSuggestDifferentRateButton(menu)
            setVoteFor(menu,it)
        })
        return true
    }

    private fun setReportClosedSubjectButton(menu: Menu, votedSuggestions: List<VotedSuggestion>) {
        if (isNewSubjectSuggestion() || isCloseSubjectSuggestionSuggested(votedSuggestions))
            menu.findItem(R.id.suggest_non_existing_subject)
                .apply { disableMenuItem(this) }
    }

    private fun setVoteFor(menu: Menu, user:User) {
        if (!isNewSubjectSuggestion())
            menu.findItem(R.id.vote_for_new_subject_suggestion)
                .apply { disableMenuItem(this) }
        showSubjectSuggestionsViewModel.votesForSuggestion.observe(this) {
            if (it.userId == user.id)
                menu.findItem(R.id.vote_for_new_subject_suggestion)
                    .apply { disableMenuItem(this) }
        }
    }

    private fun setSuggestDifferentRateButton(menu: Menu) =
        menu.findItem(R.id.suggest_different_rate)
            .also { menuItem ->
                if (isNewSubjectSuggestion())
                    disableMenuItem(menuItem)
            }
            .apply {
                if (isNewSubjectSuggestion())
                    this.title = getString(R.string.analyze_actual_rate)
            }

    private fun disableMenuItem(menuItem: MenuItem) = menuItem.apply {
        this.isEnabled = false
        this.isVisible = false
    }

    private fun isNewSubjectSuggestion() =
        getWatchedSubjectId() == NewExchangePointSuggestionExchangePointConverter.getId()

    private fun isCloseSubjectSuggestionSuggested(votedSuggestions: List<VotedSuggestion>) =
        votedSuggestions.any { it.suggestion is ClosedExchangePointSuggestion }

    private fun getWatchedSubjectId() =
        getWatchedSubject().id

    private fun getWatchedSubjectImage() = getWatchedSubject().image

    private fun getWatchedSubject() = (intent.extras!![WATCHED_SUBJECT] as WatchedSubject)

    override fun onOptionsItemSelected(item: MenuItem): Boolean =
        if (item.itemId == android.R.id.home)
            finish().run { true }
        else
            MENU_ACTIONS[item.itemId]!!.invoke(item)

    private fun suggestNonExistingSubject(menuItem: MenuItem) = showSubjectSuggestionsViewModel
        .suggest(
            createClosedExchangePointSuggestion(),
            UserSuggestionStateMarking.NEW,
            getWatchedSubjectId()
        )
        .also { disableMenuItem(menuItem) }
        .let { true }

    private fun createClosedExchangePointSuggestion() = ClosedExchangePointSuggestion(
        id = UUID.randomUUID().toString(),
        state = State.IN_PROGRESS,
        votes = 1,
        subjectId = getWatchedSubjectId(),
        createdAt = Instant.now()
    )

    private fun voteFor(menuItem: MenuItem) = getWatchedSubject().suggestions
        .forEach { showSubjectSuggestionsViewModel.voteFor(it) }
        .let { true }

    private fun suggestExchangeRateChange(menuItem: MenuItem) =
        Intent(this, RateCameraActivity::class.java)
            .apply { this.putExtra(RateCameraActivity.WATCHED_SUBJECT, getWatchedSubject()) }
            .let { this.startActivity(it) }
            .let { true }


    companion object {
        const val WATCHED_SUBJECT: String = "watchedSubject"
        const val LOGGED_USER: String = "loggedUser"
    }
}

