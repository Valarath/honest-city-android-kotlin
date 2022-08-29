package cz.city.honest.service.gateway.external

import cz.city.honest.dto.Vote
import io.reactivex.rxjava3.core.Observable

interface ExternalVoteGateway {

    fun upVote(votes: List<Vote>, userId: String, accessToken: String): Observable<Unit>
}