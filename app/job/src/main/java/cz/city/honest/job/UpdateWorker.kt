package cz.city.honest.job

import android.content.Context
import androidx.work.*
import androidx.work.rxjava3.RxWorker
import cz.city.honest.service.update.UpdateService
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.schedulers.Schedulers
import java.util.concurrent.TimeUnit

class UpdateWorkerFactory(
    private val updateService: UpdateService
) : androidx.work.WorkerFactory() {

    override fun createWorker(
        appContext: Context,
        workerClassName: String,
        workerParameters: WorkerParameters
    ): ListenableWorker? = UpdateWorker(
        context = appContext,
        workerParameters = workerParameters,
        updateService = updateService
    )
}

class UpdateWorkerManagerService(
    private val context: Context,
    private val updateWorkerFactory: UpdateWorkerFactory
) {

    fun initializeWorkerManager() = WorkManager.initialize(
        context, Configuration.Builder()
            .setWorkerFactory(updateWorkerFactory)
            .build()
    )

    fun scheduleWorker() = PeriodicWorkRequest
        .Builder(UpdateWorker::class.java, 15, TimeUnit.MINUTES)
        .setConstraints(getConstraints())
        .build()
        .let { WorkManager.getInstance(context).enqueue(it) }

    private fun getConstraints() = Constraints.Builder()
        .setRequiresBatteryNotLow(true)
        .setRequiredNetworkType(NetworkType.CONNECTED)
        .build()

}

class UpdateWorker(
    context: Context,
    workerParameters: WorkerParameters,
    private val updateService: UpdateService
) : RxWorker(context, workerParameters) {

    override fun createWork(): Single<Result> =
        updateService.update()
            .lastElement()
            .toSingle()
            .map { Result.success() }
            .onErrorReturn {
                println(it)
                Result.failure()
            }
            .subscribeOn(AndroidSchedulers.mainThread())
            .observeOn(AndroidSchedulers.mainThread())
}