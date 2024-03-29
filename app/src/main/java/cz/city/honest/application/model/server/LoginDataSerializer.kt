package cz.city.honest.application.model.server

import com.fasterxml.jackson.databind.ObjectMapper
import cz.city.honest.application.model.dto.*
import cz.city.honest.application.model.service.registration.FacebookLoginData

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

class VoteSerializer(objectMapper: ObjectMapper) :
    JsonDeserializer<Vote>(objectMapper,mapOf(
        VoteForExchangePointDelete::class.java.simpleName to VoteForExchangePointDelete::class.java,
        VoteForExchangePointRateChange::class.java.simpleName to VoteForExchangePointRateChange::class.java,
        VoteForNewExchangePoint::class.java.simpleName to VoteForNewExchangePoint::class.java
    ))
