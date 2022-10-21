package cz.city.honest.service;

import java.util.Map;

import cz.city.honest.dto.LoginData;
import cz.city.honest.service.gateway.internal.InternalAuthorizationGateway;

public class InternalAuthorizationGatewayProvider {

    public static InternalAuthorizationGateway<LoginData> provide(Map<String, InternalAuthorizationGateway<? extends LoginData>> gateways, LoginData loginData) {
        return  (InternalAuthorizationGateway<LoginData>)gateways.get(loginData.getClass().getSimpleName());
    }
}
