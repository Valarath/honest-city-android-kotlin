package cz.city.honest.job

import android.app.job.JobParameters
import android.app.job.JobService
import android.os.Looper
import cz.city.honest.service.update.UpdateService
import dagger.android.AndroidInjection
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import javax.inject.Inject

class UpdateScheduledJob() : JobService() {

    @Inject
    lateinit var updateService: UpdateService

    override fun onStartJob(p0: JobParameters?): Boolean =
        updateService
            .update()
            .subscribeOn(AndroidSchedulers.from(Looper.getMainLooper()))
            .subscribe()
            .let { true }


    override fun onStopJob(p0: JobParameters?): Boolean = true

    override fun onCreate() {
        super.onCreate()
        AndroidInjection.inject(this)
    }
}