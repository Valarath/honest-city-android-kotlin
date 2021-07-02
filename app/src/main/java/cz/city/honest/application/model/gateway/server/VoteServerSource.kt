package cz.city.honest.application.model.gateway.server

import cz.city.honest.application.model.dto.Vote
import io.reactivex.rxjava3.core.Observable
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

interface VoteServerSource  {

    @POST(VoteEndpointsUrl.UP_VOTE)
    fun upVote(request: PostUpVoteRequest,@Header("Authorization") accessToken:String): Observable<Unit>

}

data class PostUpVoteRequest(
    val votes: List<Vote>,
    val userId: String
)

object VoteEndpointsUrl {
    private const val VOTE_PREFIX = EndpointsUrl.PRIVATE + "/vote"
    const val UP_VOTE = "$VOTE_PREFIX/up-vote"
}
