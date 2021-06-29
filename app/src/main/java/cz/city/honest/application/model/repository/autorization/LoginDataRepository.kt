package cz.city.honest.application.model.repository.autorization

import cz.city.honest.application.model.dto.LoginData
import cz.city.honest.application.model.repository.DatabaseOperationProvider
import cz.city.honest.application.model.repository.Repository
import io.reactivex.rxjava3.core.Single

abstract class LoginDataRepository<LOGIN_DATA : LoginData>(
    databaseOperationProvider: DatabaseOperationProvider
) : Repository<LOGIN_DATA>(databaseOperationProvider){

    abstract fun getByUserId(userId:String): Single<LOGIN_DATA>

}