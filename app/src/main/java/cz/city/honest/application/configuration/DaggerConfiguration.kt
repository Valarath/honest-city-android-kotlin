package cz.city.honest.application.configuration

import android.app.Application
import android.content.Context
import cz.city.honest.application.model.DaggerModelConfiguration
import cz.city.honest.application.model.ModelConfiguration
import dagger.Binds
import dagger.Module
import dagger.android.AndroidInjector
import dagger.android.DaggerApplication
import javax.inject.Singleton


class BaseApplication : DaggerApplication() {
    override fun applicationInjector(): AndroidInjector<out DaggerApplication> {
        val component: ModelConfiguration =
            DaggerModelConfiguration.builder().application(this).build()
        component.inject(this)
        return component
    }
}

@Module
abstract class ContextModule {

    @Binds
    @Singleton
    abstract fun context(appInstance: Application): Context
}


