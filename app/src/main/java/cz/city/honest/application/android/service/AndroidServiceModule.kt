package cz.city.honest.application.android.service

import android.content.Context
import cz.city.honest.application.android.service.provider.AndroidPositionProvider
import cz.city.honest.application.android.service.provider.AndroidUserProvider
import cz.city.honest.application.model.service.PositionProvider
import cz.city.honest.application.model.service.UserProvider
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class AndroidServiceModule {

    @Provides
    @Singleton
    fun getPositionProvider(context: Context): PositionProvider = AndroidPositionProvider(context)

    @Provides
    @Singleton
    fun getUserProvider(): UserProvider = AndroidUserProvider()

}