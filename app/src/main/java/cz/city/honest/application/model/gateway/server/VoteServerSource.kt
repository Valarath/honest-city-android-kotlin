package cz.city.honest.application.model.gateway.server

import cz.city.honest.application.model.dto.Vote
import io.reactivex.rxjava3.core.Observable
import retrofit2.http.Header
import retrofit2.http.POST

interface VoteServerSource  {

    @POST("/up-vote")
    fun upVote(request: PostUpVoteRequest,@Header("Authorization") accessToken:String): Observable<Unit>

}

data class PostUpVoteRequest(
    val votes: List<Vote>,
    val userId: String
)

