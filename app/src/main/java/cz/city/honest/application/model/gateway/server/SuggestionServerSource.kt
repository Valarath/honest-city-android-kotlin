package cz.city.honest.application.model.gateway.server

import cz.city.honest.application.model.dto.Suggestion
import cz.city.honest.application.model.gateway.SuggestionGateway
import reactor.core.publisher.Mono
import retrofit2.http.POST
import javax.inject.Singleton

@Singleton
interface SuggestionServerSource : SuggestionGateway {

    @POST("/remove")
    fun remove(request: RemoveSuggestionRequest): Mono<Unit>

    @POST("/suggest")
    fun suggest(request: PostSuggestRequest): Mono<Unit>
}


data class RemoveSuggestionRequest(
    val suggestions: List<Suggestion>
)

data class PostSuggestRequest(
    val newExchangePointSuggestions: List<Suggestion>
)