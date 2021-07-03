package cz.city.honest.application.model.gateway.server

//import com.google.gson.*
import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.JsonDeserializer
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import cz.city.honest.application.model.gateway.GatewayModule
import org.reflections.Reflections


/*open abstract class Deserializer<DTO : Any>(private val classByName:Map<String,Class<out DTO>>) : JsonDeserializer<DTO>, JsonSerializer<DTO> {

    override fun deserialize(
        json: JsonElement,
        typeOfT: Type,
        context: JsonDeserializationContext
    ): DTO = json.asJsonObject
        .get(CLASS_NAME)
        .run { this as JsonPrimitive }
        .asString
        .run { getClassByName(this) }
        .run { context.deserialize(json, this) }

    override fun serialize(
        src: DTO,
        typeOfSrc: Type,
        context: JsonSerializationContext
    ): JsonElement = context.serialize(src)
        .run { this.asJsonObject }
        .also { it.addProperty(CLASS_NAME, src.javaClass.simpleName) }

    protected fun getClassByName(className: String) = classByName[className]!!

    companion object {
        const val CLASS_NAME = "className"
    }
}*/

open class JsonDeserializer<DTO : Any>(val objectMapper: ObjectMapper, private val classByName: Map<String, Class<out DTO>> ) :
    JsonDeserializer<DTO>() {


    override fun deserialize(jsonParser: JsonParser, context: DeserializationContext): DTO {
        val jsonNode = jsonParser.codec.readTree<JsonNode>(jsonParser)
        val returnTypeClass = getClassByName(jsonNode.get(CLASS_NAME).asText())
        return objectMapper.readValue(jsonNode.toString(), returnTypeClass)
    }

    //protected fun getObjectMapper() = GatewayModule.getBaseObjectMapper()

    protected fun getClassByName(className: String) = classByName[className]!!

    companion object {
        const val CLASS_NAME = "className"
    }

}