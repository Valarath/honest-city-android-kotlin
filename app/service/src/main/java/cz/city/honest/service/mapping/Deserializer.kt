package cz.city.honest.service.mapping

import com.fasterxml.jackson.databind.ObjectMapper
import cz.city.honest.dto.ExchangePoint
import cz.city.honest.dto.LoginData
import cz.city.honest.dto.Suggestion
import cz.city.honest.dto.WatchedSubject

class LoginDataDeserializer(objectMapper: ObjectMapper) :
    JsonDeserializer<LoginData>(objectMapper,LoginData::class.sealedSubclasses.associate { it.java.simpleName to it.java })

class WatchedSubjectDeserializer(objectMapper: ObjectMapper) :
    JsonDeserializer<WatchedSubject>(objectMapper,mapOf(ExchangePoint::class.java.simpleName to ExchangePoint::class.java))

class SuggestionDeserializer(objectMapper: ObjectMapper) :
    JsonDeserializer<Suggestion>(objectMapper,
        Suggestion::class.sealedSubclasses.associate { it.java.simpleName to it.java })
