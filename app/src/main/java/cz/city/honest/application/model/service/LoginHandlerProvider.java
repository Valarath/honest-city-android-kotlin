package cz.city.honest.application.model.service;

import java.util.Map;

import cz.city.honest.application.model.service.registration.LoginData;
import cz.city.honest.application.model.service.registration.LoginHandler;
import cz.city.honest.application.model.dto.LoginProvider;

public class LoginHandlerProvider {

    public static LoginHandler<LoginData> provide(Map<LoginProvider, LoginHandler<? extends LoginData>> handlers, LoginProvider provider) {
        return  (LoginHandler<LoginData>) handlers.get(provider);
    }
}
