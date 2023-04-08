package cz.city.honest.service.mapping

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.databind.module.SimpleModule
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.KotlinModule
import cz.city.honest.dto.LoginData
import cz.city.honest.dto.Suggestion
import cz.city.honest.dto.WatchedSubject

class ObjectMapperProvider {

    companion object {

        fun getObjectMapper() = getBaseObjectMapper()
            .also { it.registerModule(getModule(it)) }

        private fun getModule(objectMapper: ObjectMapper) = SimpleModule()
            .apply { setDeserializers(this, objectMapper) }

        private fun setDeserializers(module: SimpleModule, objectMapper: ObjectMapper) =
            module.apply {
                this.addDeserializer(
                    LoginData::class.java,
                    LoginDataSerializer(
                        objectMapper
                    )
                )
                this.addDeserializer(
                    WatchedSubject::class.java,
                    WatchedSubjectSerializer(
                        objectMapper
                    )
                )
                this.addDeserializer(
                    Suggestion::class.java,
                    SuggestionSerializer(
                        objectMapper
                    )
                )
            }

        private fun getBaseObjectMapper() = ObjectMapper()
            .also { it.registerModule(KotlinModule()) }
            .also { it.registerModule(JavaTimeModule()) }
            .also { it.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS) }
            .also { it.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false) }
    }
}