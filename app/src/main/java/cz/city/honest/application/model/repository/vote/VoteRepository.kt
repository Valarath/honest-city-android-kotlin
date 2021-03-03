package cz.city.honest.application.model.repository.vote

import cz.city.honest.application.model.repository.DatabaseOperationProvider
import cz.city.honest.application.model.repository.Repository
import cz.city.honest.mobile.model.dto.Vote
import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.core.Observable

class VoteRepository(operationProvider: DatabaseOperationProvider): Repository<Vote>(operationProvider)  {

    override fun insert(entity: Vote): Observable<Long> {
        TODO("Not yet implemented")
    }

    override fun update(entity: Vote): Observable<Int> {
        TODO("Not yet implemented")
    }

    override fun get(id: List<Long>): Flowable<Vote> {
        TODO("Not yet implemented")
    }

    override fun delete(entity: Vote): Observable<Int> {
        TODO("Not yet implemented")
    }
}