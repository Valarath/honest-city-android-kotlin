package cz.city.honest.application.android.service.authentication

import android.app.Service
import android.content.Intent
import android.os.IBinder

class AccountAuthenticationService :Service() {

    private lateinit var accountAuthenticator: AccountAuthenticator


    override fun onCreate() {
        super.onCreate()
        accountAuthenticator = AccountAuthenticator(this)
    }

    override fun onBind(intent: Intent?): IBinder = accountAuthenticator.iBinder
}