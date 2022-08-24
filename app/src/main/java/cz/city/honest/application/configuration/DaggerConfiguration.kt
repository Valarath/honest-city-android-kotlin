package cz.city.honest.application.configuration

import android.app.Application
import android.content.Context
import cz.city.honest.analyzer.AnalyzerModule
import cz.city.honest.application.model.DaggerModelConfiguration
import cz.city.honest.application.model.ModelConfiguration
import cz.city.honest.external.ServerSourceModule
import cz.city.honest.job.JobModule
import cz.city.honest.property.PropertyModule
import cz.city.honest.repository.RepositoryModule
import cz.city.honest.service.ServiceModule
import cz.city.honest.view.ActivityModule
import cz.city.honest.viewmodel.ViewModelModule
import cz.honest.city.internal.AndroidServiceModule
import dagger.Binds
import dagger.BindsInstance
import dagger.Component
import dagger.Module
import dagger.android.AndroidInjector
import dagger.android.DaggerApplication
import dagger.android.support.AndroidSupportInjectionModule
import javax.inject.Singleton


class BaseApplication : DaggerApplication() {
    override fun applicationInjector(): AndroidInjector<out DaggerApplication> {
        val component: ModuleConfiguration =
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

@Singleton
@Component(
    modules = [
        JobModule::class,
        ContextModule::class,
        AndroidSupportInjectionModule::class,
        AndroidServiceModule::class,
        ActivityModule::class,
        AnalyzerModule::class,
        ServiceModule::class,
        ServerSourceModule::class,
        RepositoryModule::class,
        ViewModelModule::class,
        PropertyModule::class
    ]
)
interface ModuleConfiguration : AndroidInjector<DaggerApplication> {

    fun inject(application: BaseApplication): Unit;

    @Component.Builder
    interface Builder {
        @BindsInstance
        fun application(application: Application): Builder
        fun build(): ModuleConfiguration
    }
}

