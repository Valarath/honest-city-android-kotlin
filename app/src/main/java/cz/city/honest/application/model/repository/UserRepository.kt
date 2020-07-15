package cz.city.honest.application.model.repository

import cz.city.honest.application.model.gateway.UserGateway
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserRepository @Inject constructor(
    val databaseOperationProvider: DatabaseOperationProvider
) : UserGateway