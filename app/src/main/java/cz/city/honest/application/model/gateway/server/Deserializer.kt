package cz.city.honest.application.model.gateway.server

import com.google.gson.*
import java.lang.reflect.Type


open abstract class Deserializer<DTO : Any>(private val classByName:Map<String,Class<out DTO>>) : JsonDeserializer<DTO>, JsonSerializer<DTO> {

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
}