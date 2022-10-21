package cz.city.honest.external.suggestion

import cz.city.honest.dto.Suggestion
import cz.city.honest.external.EndpointsUrl
import cz.city.honest.service.gateway.external.ExternalSuggestionGateway
import io.reactivex.rxjava3.core.Observable
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

class SuggestionServerSourceService(private val suggestionServerSource: SuggestionServerSource) :
    ExternalSuggestionGateway {

    override fun remove(accessToken: String, suggestions: List<Suggestion>): Observable<Unit> =
        suggestionServerSource.remove(RemoveSuggestionRequest(suggestions), accessToken)

    override fun suggest(
        accessToken: String,
        newExchangePointSuggestions: List<Suggestion>
    ): Observable<Unit> =
        suggestionServerSource.suggest(PostSuggestRequest(newExchangePointSuggestions), accessToken)

}

interface SuggestionServerSource {

    @POST(SuggestionEndpointsUrl.REMOVE)
    fun remove(
        @Body request: RemoveSuggestionRequest,
        @Header("Authorization") accessToken: String
    ): Observable<Unit>

    @POST(SuggestionEndpointsUrl.SUGGEST)
    fun suggest(
        @Body request: PostSuggestRequest,
        @Header("Authorization") accessToken: String
    ): Observable<Unit>
}

data class RemoveSuggestionRequest(
    val suggestions: List<Suggestion>
)

data class PostSuggestRequest(
    val newExchangePointSuggestions: List<Suggestion>
)

object SuggestionEndpointsUrl {
    private const val SUGGESTION_PREFIX = EndpointsUrl.PRIVATE + "/suggestion"
    const val SUGGEST = "$SUGGESTION_PREFIX/suggest"
    const val REMOVE = "$SUGGESTION_PREFIX/remove"
}