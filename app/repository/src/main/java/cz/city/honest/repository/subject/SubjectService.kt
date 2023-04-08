package cz.city.honest.repository.subject

import cz.city.honest.dto.Filter
import cz.city.honest.dto.WatchedSubject
import cz.city.honest.service.gateway.internal.InternalSubjectGateway
import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.core.Observable

class SubjectService(private val subjectRepository: SubjectRepository):InternalSubjectGateway {

    override fun getSubjects(filter: Filter): Flowable<out WatchedSubject> =
        subjectRepository.get(filter)

    override fun updateSubjects(subjects: List<WatchedSubject>):Observable<Unit> =
        Observable.fromIterable(subjects)
            .flatMap { subjectRepository.insert(it) }
            .map {  }
}