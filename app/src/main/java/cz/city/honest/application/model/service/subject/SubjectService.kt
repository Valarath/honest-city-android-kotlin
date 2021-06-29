package cz.city.honest.application.model.service.subject

import cz.city.honest.application.model.dto.*
import cz.city.honest.application.model.gateway.server.GetSubjectsRequest
import cz.city.honest.application.model.gateway.server.SubjectServerSource
import cz.city.honest.application.model.repository.subject.SubjectRepository
import cz.city.honest.application.model.service.update.PublicUpdatable
import cz.city.honest.application.model.service.RepositoryProvider

import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.core.Observable
import java.time.LocalDate


class SubjectService(
    val subjectRepositories: Map<String, SubjectRepository<out WatchedSubject>>,
    val subjectServerSource: SubjectServerSource,
    val positionProvider: PositionProvider
) : PublicUpdatable {

    fun getSubjects():  Flowable<out WatchedSubject> =
        Flowable.fromIterable(subjectRepositories.values)
            .map { addFakeSubject() }
            //.flatMap { it.get() }

    private fun addFakeSubject()  =
            ExchangePoint(
                "21", LocalDate.now(), HonestyStatus.DISHONEST,
                Position(14.423777, 50.084344),
                mutableListOf(),
                ExchangeRate(
                    "", Watched(LocalDate.now(), LocalDate.now()), mutableSetOf(
                        Rate("cze", ExchangeRateValues(1.0)),
                        Rate("eur", ExchangeRateValues(17.0)),
                        Rate("usd", ExchangeRateValues(25.0)),
                        Rate("trr", ExchangeRateValues(22.0))

                    )
                ),
                "aaa".toByteArray()
            )

    override fun update(): Observable<Unit> =
        positionProvider.provide()
            .firstOrError()
            .flatMapObservable { subjectServerSource.getSubjectsInArea(GetSubjectsRequest(it))}
            .flatMap {Observable.fromIterable( it.subjects.entries)}
            .map { RepositoryProvider.provide(
                subjectRepositories,
                it.key
            ).insertList(it.value)}
            .map {  }

}

interface PositionProvider{

    fun provide(): Observable<Position>

}