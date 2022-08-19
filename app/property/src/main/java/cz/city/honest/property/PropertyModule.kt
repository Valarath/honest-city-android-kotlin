package cz.city.honest.property

import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class PropertyModule {

    @Provides
    @Singleton
    fun getConnectionProperties(): ConnectionProperties =
        PropertyProvider.get(ConnectionProperties::class.java)

}