package cz.city.honest.application.model.service;

import java.util.Map;

import cz.city.honest.application.model.dto.LoginData;
import cz.city.honest.application.model.repository.Repository;
import cz.city.honest.application.model.repository.autorization.LoginDataRepository;

public class RepositoryProvider {

    public static <ENTITY> Repository<? super ENTITY> provide( Map<String, ? extends Repository<?>> repositories, Class<? extends ENTITY> entityClass) {
        return (Repository<? super ENTITY>) repositories.get(entityClass.getSimpleName());
    }

    public static <ENTITY extends LoginData> LoginDataRepository<? super ENTITY> provide(Map<String, ? extends LoginDataRepository<? extends ENTITY>> repositories, String entityClass) {
        return (LoginDataRepository<? super ENTITY>) repositories.get(entityClass);
    }

}
