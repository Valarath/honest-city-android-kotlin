package cz.city.honest.repository.autorization

import cz.city.honest.dto.LoginData
import cz.city.honest.repository.DatabaseOperationProvider
import cz.city.honest.repository.Repository
import io.reactivex.rxjava3.core.Maybe

abstract class LoginDataRepository<LOGIN_DATA : LoginData>(
    databaseOperationProvider: DatabaseOperationProvider
) : Repository<LOGIN_DATA>(databaseOperationProvider){

    abstract fun getByUserId(userId:String): Maybe<LOGIN_DATA>

    abstract fun getById(Id:String): Maybe<LOGIN_DATA>

}