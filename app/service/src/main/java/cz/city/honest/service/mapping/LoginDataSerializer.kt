package cz.city.honest.service.mapping

import com.fasterxml.jackson.databind.ObjectMapper
import cz.city.honest.dto.*

class LoginDataSerializer(objectMapper: ObjectMapper) :
    JsonDeserializer<LoginData>(objectMapper,mapOf(FacebookLoginData::class.java.simpleName to FacebookLoginData::class.java))

class WatchedSubjectSerializer(objectMapper: ObjectMapper) :
    JsonDeserializer<WatchedSubject>(objectMapper,mapOf(ExchangePoint::class.java.simpleName to ExchangePoint::class.java))

class SuggestionSerializer(objectMapper: ObjectMapper) :
    JsonDeserializer<Suggestion>(objectMapper,mapOf(
        NewExchangePointSuggestion::class.java.simpleName to NewExchangePointSuggestion::class.java,
        ExchangeRateSuggestion::class.java.simpleName to ExchangeRateSuggestion::class.java,
        ClosedExchangePointSuggestion::class.java.simpleName to ClosedExchangePointSuggestion::class.java
    ))
