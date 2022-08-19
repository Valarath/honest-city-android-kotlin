package cz.city.honest.external

import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.JsonDeserializer
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper


open class JsonDeserializer<DTO : Any>(val objectMapper: ObjectMapper, private val classByName: Map<String, Class<out DTO>> ) :
    JsonDeserializer<DTO>() {


    override fun deserialize(jsonParser: JsonParser, context: DeserializationContext): DTO {
        val jsonNode = jsonParser.codec.readTree<JsonNode>(jsonParser)
        val returnTypeClass = getClassByName(jsonNode.get(CLASS_NAME).asText())
        return objectMapper.readValue(jsonNode.toString(), returnTypeClass)
    }

    protected fun getClassByName(className: String) = classByName[className]!!

    companion object {
        const val CLASS_NAME = "className"
    }

}