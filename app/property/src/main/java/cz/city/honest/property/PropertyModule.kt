package cz.city.honest.property

import cz.city.honest.service.provider.PropertyProvider
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class PropertyModule {
    companion object {
        @Provides
        @Singleton
        fun getYamlPropertyProvider(): PropertyProvider = YamlPropertyProvider()
    }
}