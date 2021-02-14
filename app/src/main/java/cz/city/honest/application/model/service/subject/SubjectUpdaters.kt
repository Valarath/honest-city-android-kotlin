package cz.city.honest.application.model.service.subject

import cz.city.honest.mobile.model.dto.ExchangePoint
import cz.city.honest.mobile.model.dto.WatchedSubject


interface SubjectUpdaters<WATCHED_SUBJECT:WatchedSubject> {

    fun update(watchedSubjects:List<WATCHED_SUBJECT>)

}

class ExchangePointUpdater:SubjectUpdaters<ExchangePoint> {

    override fun update(watchedSubjects: List<ExchangePoint>) {
        
    }
}