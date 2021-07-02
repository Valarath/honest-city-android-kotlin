package cz.city.honest.application.model.gateway.server

import com.google.gson.*
import java.lang.reflect.Type


open abstract class Deserializer<DTO> : JsonDeserializer<DTO> {

    override fun deserialize(
        json: JsonElement,
        typeOfT: Type,
        context: JsonDeserializationContext
    ): DTO = json.asJsonObject
        .get(CLASS_NAME)
        .run { this as JsonPrimitive }
        .asString
        .run { getClassInstance(this) }
        .run { context.deserialize(json, this) }



    protected fun getClassInstance(className: String): Class<DTO> =
        try {
            Class.forName(className) as Class<DTO>
        } catch (cnfe: ClassNotFoundException) {
            throw JsonParseException(cnfe.message)
        }

    companion object{
        const val CLASS_NAME="className"
    }
}