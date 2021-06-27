package cz.city.honest.application.job

import android.app.job.JobParameters
import android.app.job.JobService
import cz.city.honest.application.model.service.update.UpdateService
import dagger.android.AndroidInjection
import javax.inject.Inject

class UpdateScheduledJob() : JobService() {

    @Inject
    lateinit var updateService: UpdateService

    override fun onStartJob(p0: JobParameters?): Boolean =
        updateService
            .update()
            .subscribe()
            .let { true }


    override fun onStopJob(p0: JobParameters?): Boolean {
        TODO("Not yet implemented")
    }

    override fun onCreate() {
        super.onCreate()
        AndroidInjection.inject(this)
    }
}