package cz.city.honest.application.model.gateway

import cz.city.honest.mobile.model.dto.Vote
import reactor.core.publisher.Mono

interface VoteGateway {

    fun upVote(request: PostUpVoteRequest): Mono<Unit>
}

data class PostUpVoteRequest(
    val votes: List<Vote>,
    val userId: Long
)