package cz.city.honest.application.main

import android.content.Intent
import android.os.Bundle
import cz.city.honest.job.UpdateWorkerManagerService
import cz.city.honest.view.MapActivity
import dagger.android.support.DaggerAppCompatActivity
import javax.inject.Inject

class MainActivity : DaggerAppCompatActivity(){

    @Inject
    lateinit var updateWorkerManagerService: UpdateWorkerManagerService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        updateWorkerManagerService.scheduleWorker()
        this.startActivity(Intent(this, MapActivity::class.java))
    }
}