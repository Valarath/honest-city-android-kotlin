package cz.city.honest.application.view.login

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.facebook.login.widget.LoginButton
import cz.city.honest.application.R
import cz.city.honest.application.model.service.registration.FacebookLoginData
import cz.city.honest.application.view.user.UserDetailActivity
import cz.city.honest.application.viewmodel.LoginViewModel
import cz.city.honest.application.model.dto.LoginProvider
import cz.city.honest.application.model.dto.User
import dagger.android.support.DaggerAppCompatActivity
import javax.inject.Inject


class LoginActivity : DaggerAppCompatActivity() {

    private val callbackManager: CallbackManager = CallbackManager.Factory.create()

    private lateinit var loginViewModel: LoginViewModel

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        setLoginViewModel()
        setLoginButton()
    }

    private fun setLoginViewModel() {
        loginViewModel = ViewModelProvider(this, viewModelFactory).get(LoginViewModel::class.java)
        loginViewModel.loggedUser.observe(this, processLoggedUser())
    }

    private fun processLoggedUser(): Observer<User> {
        return Observer {
            this.startActivity(Intent(this, UserDetailActivity::class.java))
        }
    }

    private fun setLoginButton() =
        findViewById<LoginButton>(R.id.login_button)
            .apply { setPermissions(FACEBOOK_PERMISSIONS) }
            .apply {
                registerCallback(
                    callbackManager,
                    LoginResultFacebookCallback(this@LoginActivity, loginViewModel)
                )
            }

    override fun onActivityResult(
        requestCode: Int,
        resultCode: Int,
        data: Intent?
    ) {
        super.onActivityResult(requestCode, resultCode, data)
        callbackManager.onActivityResult(requestCode, resultCode, data)
    }

    companion object{
        val FACEBOOK_PERMISSIONS = listOf("name,email")
    }

}

class LoginResultFacebookCallback(
    private val activity: Activity,
    private val loginViewModel: LoginViewModel
) : FacebookCallback<LoginResult> {
    override fun onSuccess(result: LoginResult) {
        if (result.accessToken.isExpired)
            LoginManager.getInstance().logInWithReadPermissions(activity, LoginActivity.FACEBOOK_PERMISSIONS)
        loginViewModel.loginUser(FacebookLoginData(result.accessToken), LoginProvider.FACEBOOK)
    }

    override fun onCancel() {
        showMessage(activity.getString(R.string.facebook_login_cancelled))
    }

    override fun onError(error: FacebookException) {
        showMessage(activity.getString(R.string.facebook_login_error))
    }

    private fun showMessage(message:String) = Toast.makeText(
        activity, message,
        Toast.LENGTH_SHORT
    ).show()
}