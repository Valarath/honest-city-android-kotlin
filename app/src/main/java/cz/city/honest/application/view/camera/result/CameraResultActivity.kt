package cz.city.honest.application.view.camera.result

import android.os.Bundle
import androidx.fragment.app.FragmentTransaction
import cz.city.honest.application.R
import dagger.android.support.DaggerAppCompatActivity

class CameraResultActivity : DaggerAppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_camera_result)
        val transaction: FragmentTransaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.camera_result, CameraResultFragment())
        transaction.commit()
    }


    companion object {
        const val EXCHANGE_RATE_RESULT:String = "exchangeRateResult"
        const val WATCHED_SUBJECT:String = "watchedSubject"
    }
}