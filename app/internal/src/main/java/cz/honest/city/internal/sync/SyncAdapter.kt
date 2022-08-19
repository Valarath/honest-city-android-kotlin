package cz.honest.city.internal.sync

import android.accounts.Account
import android.content.*
import android.os.Bundle
import cz.city.honest.service.update.UpdateService

class SyncAdapter @JvmOverloads constructor(
    context: Context,
    autoInitialize: Boolean,
    allowParallelSyncs: Boolean = false,
    private val updateService: UpdateService
) : AbstractThreadedSyncAdapter(context, autoInitialize, allowParallelSyncs) {

    override fun onPerformSync(
        account: Account?,
        extras: Bundle?,
        authority: String?,
        provider: ContentProviderClient?,
        syncResult: SyncResult?
    ) {
        updateService.update().subscribe()
    }


}