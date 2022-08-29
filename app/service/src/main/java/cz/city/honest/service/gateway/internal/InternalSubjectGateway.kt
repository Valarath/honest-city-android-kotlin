package cz.city.honest.service.gateway.internal

import cz.city.honest.dto.Filter
import cz.city.honest.dto.WatchedSubject
import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.core.Observable

interface InternalSubjectGateway {

    fun getSubjects(filter: Filter): Flowable<out WatchedSubject>

    fun updateSubjects(subjects: List<WatchedSubject>): Observable<Unit>
}