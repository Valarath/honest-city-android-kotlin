package cz.city.honest.external

import cz.city.honest.dto.Vote
import cz.city.honest.service.gateway.external.ExternalVoteGateway
import io.reactivex.rxjava3.core.Observable
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

class VoteServerSourceService(private val voteServerSource: VoteServerSource) :
    ExternalVoteGateway {

    override fun upVote(votes: List<Vote>, userId: String, accessToken: String): Observable<Unit> =
        voteServerSource.upVote(PostUpVoteRequest(votes,userId),accessToken)
}

interface VoteServerSource {

    @POST(VoteEndpointsUrl.UP_VOTE)
    fun upVote(
        @Body request: PostUpVoteRequest,
        @Header("Authorization") accessToken: String
    ): Observable<Unit>

}

data class PostUpVoteRequest(
    val votes: List<Vote>,
    val userId: String
)

object VoteEndpointsUrl {
    private const val VOTE_PREFIX = EndpointsUrl.PRIVATE + "/vote"
    const val UP_VOTE = "$VOTE_PREFIX/up-vote"
}
