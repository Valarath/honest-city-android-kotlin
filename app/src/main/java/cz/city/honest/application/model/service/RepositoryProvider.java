package cz.city.honest.application.model.service;

import java.util.Map;

import cz.city.honest.application.model.dto.LoginData;
import cz.city.honest.application.model.dto.WatchedSubject;
import cz.city.honest.application.model.repository.Repository;
import cz.city.honest.application.model.repository.autorization.LoginDataRepository;
import cz.city.honest.application.model.repository.subject.SubjectRepository;

public class RepositoryProvider {

    public static <ENTITY> Repository<? super ENTITY> provide( Map<String, ? extends Repository<?>> repositories, Class<? extends ENTITY> entityClass) {
        return (Repository<? super ENTITY>) repositories.get(entityClass.getSimpleName());
    }

    public static <ENTITY extends WatchedSubject> SubjectRepository<? super ENTITY> provide(Map<String, ? extends SubjectRepository<? extends ENTITY>> repositories, String entityClass) {
        return (SubjectRepository<? super ENTITY>) repositories.get(entityClass);
    }

    public static <ENTITY extends LoginData> LoginDataRepository<? super ENTITY> provideLoginDataRepository(Map<String, ? extends LoginDataRepository<? extends ENTITY>> repositories, String entityClass) {
        return (LoginDataRepository<? super ENTITY>) repositories.get(entityClass);
    }

}
