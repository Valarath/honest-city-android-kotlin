package cz.city.honest.application.android.service.sync

import android.app.Service
import android.content.Intent
import android.os.IBinder
import cz.city.honest.application.model.service.update.UpdateService
import javax.inject.Inject

class SyncAdapterService:Service() {

    @Inject
    lateinit var updateService: UpdateService

    private lateinit var syncAdapter: SyncAdapter

    override fun onCreate() {
        super.onCreate()
        syncAdapter = SyncAdapter(context = applicationContext,autoInitialize = true,updateService = updateService)
    }

    override fun onBind(intent: Intent?): IBinder? = syncAdapter.syncAdapterBinder
}