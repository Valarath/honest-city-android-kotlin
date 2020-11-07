package cz.city.honest.application.view.user

import android.os.Bundle
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.viewpager.widget.ViewPager
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.tabs.TabLayout
import cz.city.honest.application.R
import cz.city.honest.application.view.user.ui.main.UserDetailPagerAdapter
import cz.city.honest.application.viewmodel.UserDetailViewModel
import dagger.android.support.DaggerAppCompatActivity
import javax.inject.Inject

class UserDetailActivity : DaggerAppCompatActivity() {


    protected lateinit var userDetailViewModel: UserDetailViewModel

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_detail)
        val sectionsPagerAdapter = UserDetailPagerAdapter(this, supportFragmentManager)
        val viewPager: ViewPager = findViewById(R.id.view_pager)
        viewPager.adapter = sectionsPagerAdapter
        val tabs: TabLayout = findViewById(R.id.tabs)
        tabs.setupWithViewPager(viewPager)
        /*val fab: FloatingActionButton = findViewById(R.id.fab)
        fab.setOnClickListener { view ->
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show()
        }*/
        userDetailViewModel =
            ViewModelProvider(this, viewModelFactory).get(UserDetailViewModel::class.java)
        userDetailViewModel.userData.observe(this, Observer {
            setTitle("${it.username} | ${it.score} ")
        })
    }
}