package cz.city.honest.application.model.property

import org.yaml.snakeyaml.Yaml

class PropertyProvider {

    companion object {
        fun <T> get(propertyType: Class<T>): T =
            Yaml().loadAs(getResource(propertyType.simpleName.decapitalize()), propertyType)

        private fun getResource(fileName: String) = PropertyProvider::class.java
            .getResource("/${fileName}.yaml")
            .openStream()
    }
}

data class ConnectionProperties(var baseUrl:String, var receiveDataAtHours:Int){
    constructor():this("",0)
}