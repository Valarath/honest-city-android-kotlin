package cz.city.honest.application.model.service;

import java.util.Map;

import cz.city.honest.application.model.dto.LoginData;
import cz.city.honest.application.model.service.registration.LoginHandler;

public class LoginHandlerProvider {

    public static LoginHandler<LoginData> provide(Map<String, LoginHandler<? extends LoginData>> handlers, LoginData loginData) {
        return  (LoginHandler<LoginData>)handlers.get(loginData.getClass().getSimpleName());
    }
}
