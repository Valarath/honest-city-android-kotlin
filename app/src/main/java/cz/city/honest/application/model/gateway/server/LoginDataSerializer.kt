package cz.city.honest.application.model.gateway.server

import cz.city.honest.application.model.dto.LoginData
import cz.city.honest.application.model.service.registration.FacebookLoginData

class LoginDataSerializer: Deserializer<LoginData> (mapOf(FacebookLoginData::class.java.simpleName to FacebookLoginData::class.java))
