package cz.city.honest.application.android.service.sync

import android.accounts.Account
import android.accounts.AccountManager
import android.content.*
import android.os.Bundle
import io.reactivex.rxjava3.core.Observable

class SyncAdapter @JvmOverloads constructor(
    context: Context,
    autoInitialize: Boolean,
    allowParallelSyncs: Boolean = false
) : AbstractThreadedSyncAdapter(context, autoInitialize, allowParallelSyncs) {


    private val accountManager = AccountManager.get(context)
    private val contentResolver: ContentResolver = context.contentResolver

    override fun onPerformSync(
        account: Account?,
        extras: Bundle?,
        authority: String?,
        provider: ContentProviderClient?,
        syncResult: SyncResult?
    ) {
        //TODO tady udelej veskerej sync
        accountManager.blockingGetAuthToken(account,"access",false)
        Observable.just(accountManager.blockingGetAuthToken(account,"access",false)).subscribe {}
    }


}