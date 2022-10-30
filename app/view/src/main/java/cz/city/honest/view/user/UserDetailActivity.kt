package cz.city.honest.view.user

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.viewpager.widget.ViewPager
import com.facebook.login.widget.LoginButton
import com.google.android.material.tabs.TabLayout
import cz.city.honest.dto.FacebookLoginData
import cz.city.honest.dto.LoginData
import cz.city.honest.view.MapActivity
import cz.city.honest.view.R
import cz.city.honest.view.login.LoginActivity
import cz.city.honest.view.user.ui.main.UserDetailPagerAdapter
import cz.city.honest.viewmodel.UserDetailViewModel

import dagger.android.support.DaggerAppCompatActivity
import javax.inject.Inject

class UserDetailActivity : DaggerAppCompatActivity() {

    protected lateinit var userDetailViewModel: UserDetailViewModel

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    private val MENU_ACTIONS: Map<Int, (menuItem: MenuItem) -> Boolean> = mapOf(
        R.id.user_detail_logout to ::logout,
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_detail)
        val sectionsPagerAdapter = UserDetailPagerAdapter(this, supportFragmentManager)
        val viewPager: ViewPager = findViewById(R.id.view_pager)
        viewPager.adapter = sectionsPagerAdapter
        val tabs: TabLayout = findViewById(R.id.tabs)
        tabs.setupWithViewPager(viewPager)
        userDetailViewModel =
            ViewModelProvider(this, viewModelFactory).get(UserDetailViewModel::class.java)
        userDetailViewModel.userData.observe(this, Observer {
            setTitle("${it.username} | ${it.score} ")
        })
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean =
        menuInflater.inflate(R.menu.user_menu, menu)
            .run { true }

    override fun onOptionsItemSelected(item: MenuItem): Boolean =
        if (item.itemId == android.R.id.home)
            finish().run { true }
        else
            MENU_ACTIONS[item.itemId]!!.invoke(item)

    private fun logout(menuItem: MenuItem) =
        userDetailViewModel.userData.observe(this, Observer {user ->
            userDetailViewModel.logout()
                .also { logoutFromIdentityProvider(user.loginData) }
                .also { this.startActivity(Intent(this, MapActivity::class.java)) }
        })
            .let { true }

    private fun logoutFromIdentityProvider(loginData: LoginData) =
        loginData.also {
            if (it is FacebookLoginData)
                findViewById<LoginButton>(R.id.facebookButton).performClick()
        }

}