package cz.city.honest.service.gateway.external

import cz.city.honest.dto.Position
import cz.city.honest.dto.Suggestion
import cz.city.honest.dto.WatchedSubject
import io.reactivex.rxjava3.core.Observable

interface ExternalSubjectGateway {

    fun getSubjectsInArea(position: Position): Observable<Subjects>
}

data class Subjects(
    val subjects: MutableMap<String, List<WatchedSubject>>,
    val newSubjectSuggestions: MutableMap<String, List<Suggestion>>
)