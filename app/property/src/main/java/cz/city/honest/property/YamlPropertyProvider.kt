package cz.city.honest.property

import cz.city.honest.service.provider.PropertyProvider
import org.yaml.snakeyaml.Yaml
import java.util.*

class YamlPropertyProvider : PropertyProvider {

    override fun <T> providePropertyOfType(propertyType: Class<T>): T = get(propertyType)

    companion object {
        fun <T> get(propertyType: Class<T>): T =
            Yaml().loadAs(getResource(propertyType.simpleName.replaceFirstChar { it.lowercase(Locale.getDefault()) }), propertyType)

        private fun getResource(fileName: String) = YamlPropertyProvider::class.java
            .getResource("/${fileName}.yaml")
            .openStream()
    }
}
