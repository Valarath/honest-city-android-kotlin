package cz.city.honest.view.camera.subject

import android.os.Bundle
import androidx.fragment.app.FragmentTransaction
import cz.city.honest.view.R
import cz.city.honest.view.camera.rate.RateCameraFragment
import dagger.android.support.DaggerAppCompatActivity

class SubjectActivity : DaggerAppCompatActivity(){

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_subject_name_camera)
        val transaction: FragmentTransaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.camera, SubjectNameCameraFragment())
        transaction.commit()
    }
}