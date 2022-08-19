package cz.city.honest.view.login

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.facebook.AccessToken
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.facebook.login.widget.LoginButton
import cz.city.honest.dto.FacebookLoginData
import cz.city.honest.view.R
import cz.city.honest.dto.User
import cz.city.honest.view.user.UserDetailActivity
import cz.city.honest.view.MapActivity
import cz.city.honest.viewmodel.LoginUserSubscribeHandler
import cz.city.honest.viewmodel.LoginViewModel
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
        callbackManager.onActivityResult(requestCode, resultCode, data)
        super.onActivityResult(requestCode, resultCode, data)
    }

    companion object {
        val FACEBOOK_PERMISSIONS = listOf("email")
    }

}

class LoginResultFacebookCallback(
    private val activity: Activity,
    private val loginViewModel: LoginViewModel
) : FacebookCallback<LoginResult> {
    override fun onSuccess(result: LoginResult) {
        if (AccessToken.getCurrentAccessToken() != null && result.accessToken.isExpired)
            LoginManager.getInstance()
                .logInWithReadPermissions(activity, LoginActivity.FACEBOOK_PERMISSIONS)
        else
            loginUser(result.accessToken)
    }

    private fun loginUser(accessToken: AccessToken) = loginViewModel.loginUser(
        accessToken.userId,
        FacebookLoginData::class.java,
        getLoginUserSubscribeHandler(accessToken)
    )

    private fun getLoginUserSubscribeHandler(accessToken: AccessToken) =
        LoginUserSubscribeHandler(
            loginUser = { user -> loginUser(user, accessToken) },
            exceptionHandler = { exception -> handleError(exception) },
            registerUser = { registerUser(accessToken) }
        )

    private fun registerUser(accessToken: AccessToken) {
        loginViewModel.registerUser(getFacebookLoginData(accessToken, NEW_USER_ID))
            .also { activity.startActivity(Intent(activity, MapActivity::class.java)) }
    }

    private fun loginUser(user: User, accessToken: AccessToken) =
        loginViewModel.loginUser(user.copy(loginData = getFacebookLoginData(accessToken, user.id)))
            .also { activity.startActivity(Intent(activity, MapActivity::class.java)) }

    private fun getFacebookLoginData(accessToken: AccessToken, userId: String) =
        FacebookLoginData(
            accessToken.token,
            accessToken.userId,
            userId
        )

    override fun onCancel() {
        showMessage(activity.getString(R.string.facebook_login_cancelled))
    }

    override fun onError(error: FacebookException) {
        showMessage(activity.getString(R.string.facebook_login_error))
    }

    fun handleError(error: Throwable) {
        showMessage(activity.getString(R.string.facebook_login_error))
    }

    private fun showMessage(message: String) = Toast.makeText(
        activity, message,
        Toast.LENGTH_SHORT
    ).show()

    companion object {
        const val NEW_USER_ID = ""
    }
}