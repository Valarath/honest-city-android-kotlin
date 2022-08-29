package cz.city.honest.repository.subject

import cz.city.honest.dto.Filter
import cz.city.honest.dto.WatchedSubject
import cz.city.honest.repository.RepositoryProvider
import cz.city.honest.service.gateway.internal.InternalSubjectGateway
import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.core.Observable

class SubjectService(private val subjectRepositories: Map<String, SubjectRepository<out WatchedSubject>>):InternalSubjectGateway {

    override fun getSubjects(filter: Filter): Flowable<out WatchedSubject> =
        Flowable.fromIterable(subjectRepositories.values)
            .flatMap { it.get(filter) }

    override fun updateSubjects(subjects: List<WatchedSubject>):Observable<Unit> =
        Observable.fromIterable(subjects)
            .flatMap { RepositoryProvider.provide(subjectRepositories, it.getClassName()).insert(it) }
            .map {  }
}