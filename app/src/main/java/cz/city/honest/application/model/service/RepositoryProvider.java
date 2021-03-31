package cz.city.honest.application.model.service;

import java.util.Map;

import cz.city.honest.application.model.repository.Repository;

public class RepositoryProvider {

    public static <ENTITY>Repository<? super ENTITY> provide( Map<String, ? extends Repository<?>> repositories, Class<? extends ENTITY> entityClass) {
        return (Repository<? super ENTITY>) repositories.get(entityClass.getSimpleName());
    }

}
