package cz.city.honest.application.view.filter

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.preference.PreferenceFragmentCompat
import cz.city.honest.application.R
import dagger.android.support.DaggerAppCompatActivity

class FilterActivity : DaggerAppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.filter_activity)
        supportFragmentManager
                .beginTransaction()
                .replace(R.id.filter, FilterFragment())
                .commit()
    }

}