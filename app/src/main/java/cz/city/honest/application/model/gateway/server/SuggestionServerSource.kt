package cz.city.honest.application.model.gateway.server

import cz.city.honest.application.model.dto.Suggestion
import cz.city.honest.application.model.gateway.SuggestionGateway
import io.reactivex.rxjava3.core.Observable
import reactor.core.publisher.Mono
import retrofit2.http.Header
import retrofit2.http.POST

interface SuggestionServerSource : SuggestionGateway {

    @POST("/remove")
    fun remove(request: RemoveSuggestionRequest,@Header("Authorization") accessToken:String): Observable<Unit>

    @POST("/suggest")
    fun suggest(request: PostSuggestRequest, @Header("Authorization") accessToken:String): Observable<Unit>
}


data class RemoveSuggestionRequest(
    val suggestions: List<Suggestion>
)

data class PostSuggestRequest(
    val newExchangePointSuggestions: List<Suggestion>
)