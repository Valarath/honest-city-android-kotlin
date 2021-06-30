package cz.city.honest.application.android.service.sync

import android.accounts.Account
import android.accounts.AccountManager
import android.content.*
import android.os.Bundle
import cz.city.honest.application.model.service.update.UpdateService
import io.reactivex.rxjava3.core.Observable

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