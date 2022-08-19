package cz.city.honest.view.camera

import android.os.Bundle
import androidx.fragment.app.FragmentTransaction
import cz.city.honest.view.R
import dagger.android.support.DaggerAppCompatActivity

class CameraActivity : DaggerAppCompatActivity(){

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_camera)
        val transaction: FragmentTransaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.camera, CameraFragment())
        transaction.commit()
    }

    companion object{
        const val WATCHED_SUBJECT:String ="watchedSubject"
    }
}