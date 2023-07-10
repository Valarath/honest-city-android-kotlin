package cz.city.honest.service

abstract class BaseService {

    fun <T> getService(services: Map<String, T>, expectedType: Class<in T>): T =
        services[expectedType.simpleName] ?: throw ServiceNotFoundException()

}

class ServiceNotFoundException : RuntimeException()