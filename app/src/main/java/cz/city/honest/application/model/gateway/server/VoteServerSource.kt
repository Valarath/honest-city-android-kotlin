package cz.city.honest.application.model.gateway.server

import cz.city.honest.application.model.gateway.PostUpVoteRequest
import cz.city.honest.application.model.gateway.VoteGateway
import reactor.core.publisher.Mono
import retrofit2.http.POST

interface VoteServerSource : VoteGateway {

    @POST("/up-vote")
    override fun upVote(request: PostUpVoteRequest): Mono<Unit>

}

