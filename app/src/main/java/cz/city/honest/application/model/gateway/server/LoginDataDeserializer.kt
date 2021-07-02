package cz.city.honest.application.model.gateway.server

import cz.city.honest.application.model.dto.LoginData

class LoginDataDeserializer<LOGIN_DATA : LoginData> : Deserializer<LOGIN_DATA>()

