package cz.city.honest.external

import cz.city.honest.dto.Suggestion
import cz.city.honest.dto.WatchedSubject
import io.reactivex.rxjava3.core.Observable
import retrofit2.http.GET
import retrofit2.http.QueryMap


interface SubjectServerSource {

    @GET(SubjectEndpointsUrl.SUBJECT_IN_AREA)
    fun getSubjectsInArea(@QueryMap request: Map<String, String>): Observable<GetSubjectsResponse>
}

data class GetSubjectsResponse(
    val subjects: MutableMap<String, List<WatchedSubject>>,
    val newSubjectSuggestions: MutableMap<String, List<Suggestion>>
)

object SubjectEndpointsUrl {
    private const val SUBJECT_PREFIX = EndpointsUrl.PUBLIC + "/subject"
    const val SUBJECT_IN_AREA = "$SUBJECT_PREFIX/subject-in-area"
}
