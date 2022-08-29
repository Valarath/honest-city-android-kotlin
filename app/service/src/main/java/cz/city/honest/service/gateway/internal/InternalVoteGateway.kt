package cz.city.honest.service.gateway.internal

import cz.city.honest.dto.Suggestion
import cz.city.honest.dto.User
import cz.city.honest.dto.Vote
import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.core.Observable

interface InternalVoteGateway {

    fun updateVotes(user: User): Flowable<out Vote>

    fun update(vote: Vote): Observable<Unit>

    fun vote(vote: Vote): Observable<Unit>

    fun delete(vote: Vote): Observable<Unit>

    fun delete(suggestion: Suggestion, user: User): Observable<Unit>

    fun getUserSubjectVotes(subjectId: String, userId: String): Observable<out Vote>

}