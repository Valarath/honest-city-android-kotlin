package cz.city.honest.application.model.service

import cz.city.honest.application.model.repository.SubjectRepository
import cz.city.honest.mobile.model.dto.*
import io.reactivex.rxjava3.core.Observable
import java.time.LocalDate


class SubjectService(val subjectRepository: SubjectRepository) {

    fun getSubjects2(): Observable<Map<Class<out WatchedSubject>, List<WatchedSubject>>> =
        Observable.just(mutableMapOf<Class<out WatchedSubject>, List<WatchedSubject>>())
            //.map { addSubjects(it) }
            .map { addFakeSubject(it) }

    private fun addSubjects(subjects: MutableMap<Class<out WatchedSubject>, List<WatchedSubject>>): MutableMap<Class<out WatchedSubject>, List<WatchedSubject>> {
        subjectRepository.getSubjects().map { subjects.put(ExchangePoint::class.java, it) }
        return subjects
    }

    private fun addFakeSubject(subjects: MutableMap<Class<out WatchedSubject>, List<WatchedSubject>>): MutableMap<Class<out WatchedSubject>, List<WatchedSubject>> {
        subjects[ExchangePoint::class.java] = listOf(
            ExchangePoint(
                21, LocalDate.now(), HonestyStatus.DISHONEST,
                Position(14.423777, 50.084344),
                mutableListOf(),
                ExchangeRate(
                    23, Watched(LocalDate.now(), LocalDate.now()), mutableSetOf(
                        Rate("CZE", ExchangeRateValues(1.0)),
                        Rate("EUR", ExchangeRateValues(17.0)),
                        Rate("USD", ExchangeRateValues(25.0)),
                        Rate("TRR", ExchangeRateValues(22.0))

                    )
                ),
                "aaa".toByteArray()
            )
        )
        return subjects
    }

}