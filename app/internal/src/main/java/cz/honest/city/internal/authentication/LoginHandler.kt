package cz.honest.city.internal.authentication

import android.accounts.Account
import android.accounts.AccountManager
import android.content.Context
import android.os.Bundle
import cz.city.honest.dto.LoginData
import cz.city.honest.dto.User
import cz.city.honest.service.gateway.internal.InternalAuthorizationGateway
import io.reactivex.rxjava3.core.Maybe
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Single

abstract class LoginHandler<DATA : LoginData>(
    protected val context: Context,
    val accountManager: AccountManager = AccountManager.get(context)
) : InternalAuthorizationGateway<DATA> {

    protected fun registerAccountInAccountManager(
        user: User,
        accessToken: String
    ) = Observable.just(toAccount(user))
        .flatMap { addToAccountManager(it, accessToken) }
        .map { user }

    protected fun updateInAccountManager(
        user: User,
        accessToken: String
    ) = Observable.fromArray(*accountManager.accounts)
        .filter { it.name == user.username }
        .switchIfEmpty { addToAccountManager(toAccount(user), accessToken) }
        .map { accountManager.setAuthToken(it, AUTH_TOKEN_TYPE, accessToken) }
        .map { user }

    protected fun invalidateAuthenticationToken(user: User) =
        getAuthenticationToken(user)
            .map { accountManager.invalidateAuthToken(AUTH_TOKEN_TYPE, it) }
            .map { user.logged = false }
            .map { user }

    private fun addToAccountManager(account: Account, accessToken: String) =
        Observable.just(account)
            .map { accountManager.addAccountExplicitly(account, accessToken, Bundle()) }
            .map { accountManager.setAuthToken(account,AUTH_TOKEN_TYPE,accessToken) }

    private fun toAccount(user: User) =
        Account(user.username, "honest.city.cz")

    override fun getAuthenticationToken(user: User) =
        Maybe.just(accountManager.accounts)
            .map { it.filter { it.name == user.username } }
            .flatMap {
                if (it.isEmpty())
                    Maybe.empty()
                else
                    Maybe.just(accountManager.peekAuthToken(it.first(), AUTH_TOKEN_TYPE))
            }

    companion object {
        const val AUTH_TOKEN_TYPE = "USER_ACCESS"
    }
}
