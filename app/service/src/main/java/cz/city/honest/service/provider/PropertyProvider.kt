package cz.city.honest.service.provider

interface PropertyProvider {

    fun <T> providePropertyOfType(propertyType: Class<T>): T

}