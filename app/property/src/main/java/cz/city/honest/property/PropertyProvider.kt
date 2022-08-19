package cz.city.honest.property

import org.yaml.snakeyaml.Yaml
import java.util.*

class PropertyProvider {

    companion object {
        fun <T> get(propertyType: Class<T>): T =
            Yaml().loadAs(getResource(propertyType.simpleName.replaceFirstChar { it.lowercase(Locale.getDefault()) }), propertyType)

        private fun getResource(fileName: String) = PropertyProvider::class.java
            .getResource("/${fileName}.yaml")
            .openStream()
    }
}

data class ConnectionProperties(var baseUrl:String, var receiveDataAtHours:Long){
    constructor():this("",0)
}