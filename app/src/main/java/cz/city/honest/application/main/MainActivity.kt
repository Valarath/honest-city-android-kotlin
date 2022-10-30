package cz.city.honest.application.main

import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.core.app.ActivityCompat
import cz.city.honest.application.configuration.Permissions
import cz.city.honest.job.UpdateWorkerManagerService
import cz.city.honest.view.MapActivity
import dagger.android.support.DaggerAppCompatActivity
import javax.inject.Inject

class MainActivity : DaggerAppCompatActivity() {

    @Inject
    lateinit var updateWorkerManagerService: UpdateWorkerManagerService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        askForPermissions()
    }

    private fun askForPermissions() = Permissions.PERMISSIONS
        .filter { isPermissionGranted(it) }
        .toTypedArray()
        .also {
            if (it.isNotEmpty())
                ActivityCompat.requestPermissions(this, it, 1)
            else
                startApplication()
        }

    private fun startApplication(){
        updateWorkerManagerService.scheduleWorker()
        this.startActivity(Intent(this, MapActivity::class.java))
    }

    private fun isPermissionGranted(permission: String) =
        ActivityCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        askForPermissions()
    }

}