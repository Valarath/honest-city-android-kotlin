package cz.city.honest.application.view.user.ui.main

import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import cz.city.honest.application.viewmodel.UserDetailViewModel
import dagger.android.support.DaggerAppCompatDialogFragment
import javax.inject.Inject

sealed class UserDetailFragment() : DaggerAppCompatDialogFragment() {

    protected lateinit var userDetailViewModel: UserDetailViewModel

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        userDetailViewModel =
            ViewModelProvider(this, viewModelFactory).get(UserDetailViewModel::class.java)
    }
}

class UserDetailSettingsFragment() : UserDetailFragment()

class UserDetailSuggestionsFragment() : UserDetailFragment()