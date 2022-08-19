package cz.city.honest.service;

import java.util.Map;

import cz.city.honest.dto.LoginData;
import cz.city.honest.service.registration.LoginHandler;

public class LoginHandlerProvider {

    public static LoginHandler<LoginData> provide(Map<String, LoginHandler<? extends LoginData>> handlers, LoginData loginData) {
        return  (LoginHandler<LoginData>)handlers.get(loginData.getClass().getSimpleName());
    }
}
