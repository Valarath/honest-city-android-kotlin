package cz.city.honest.application.model.server

import cz.city.honest.application.model.gateway.VoteGateway
import cz.city.honest.mobile.model.dto.Vote
import reactor.core.publisher.Mono
import retrofit2.http.POST
import javax.inject.Singleton

@Singleton
interface VoteServerSource : VoteGateway {

    @POST("/up-vote")
    fun upVote(request: PostUpVoteRequest): Mono<Unit>

}

data class PostUpVoteRequest(
    val votes: List<Vote>,
    val userId: Long
)