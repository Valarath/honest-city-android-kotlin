package cz.city.honest.application.model.repository

import cz.city.honest.application.model.gateway.UserGateway

class UserRepository(
    val databaseOperationProvider: DatabaseOperationProvider
) : UserGateway