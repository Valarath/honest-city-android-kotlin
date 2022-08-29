package cz.city.honest.service.gateway.external

import cz.city.honest.dto.Suggestion
import io.reactivex.rxjava3.core.Observable

interface ExternalSuggestionGateway {

    fun remove( accessToken:String, suggestions: List<Suggestion>): Observable<Unit>

    fun suggest( accessToken:String, newExchangePointSuggestions: List<Suggestion>): Observable<Unit>
}