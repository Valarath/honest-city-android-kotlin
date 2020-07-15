package cz.city.honest.application.model.repository

import cz.city.honest.application.model.gateway.VoteGateway

class VoteRepository(
    val databaseOperationProvider: DatabaseOperationProvider
) : VoteGateway {


}