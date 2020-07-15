package cz.city.honest.application.view.detail

import android.os.Bundle
import androidx.viewpager.widget.ViewPager
import com.google.android.material.tabs.TabLayout
import cz.city.honest.application.R
import cz.city.honest.application.view.detail.ui.main.SubjectPagerAdapter
import dagger.android.support.DaggerAppCompatActivity


class SubjectDetailActivity : DaggerAppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_subject_detail)
        val sectionsPagerAdapter = SubjectPagerAdapter(this, supportFragmentManager)
        val viewPager: ViewPager = findViewById(R.id.view_pager)
        viewPager.adapter = sectionsPagerAdapter
        val tabs: TabLayout = findViewById(R.id.tabs)
        tabs.setupWithViewPager(viewPager)
    }


    companion object {
        const val INTENT_SUBJECT: String = "intentSubject"
    }
}

