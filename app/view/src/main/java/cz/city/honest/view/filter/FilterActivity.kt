package cz.city.honest.view.filter

import android.os.Bundle
import cz.city.honest.view.R
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