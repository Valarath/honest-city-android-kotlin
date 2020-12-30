package cz.city.honest.application.model.property

import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class PropertyModule {

    @Provides
    @Singleton
    fun getAuthorityGateway():ConnectionProperties = PropertyProvider.get(ConnectionProperties::class.java)

}