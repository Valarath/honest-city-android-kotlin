package cz.city.honest.service

abstract class BaseService {

    fun <T> getService(services: Map<Class<out T>, T>, expectedType: Class<T>): T =
        services[expectedType] ?: throw ServiceNotFoundException()
}

class ServiceNotFoundException : RuntimeException()