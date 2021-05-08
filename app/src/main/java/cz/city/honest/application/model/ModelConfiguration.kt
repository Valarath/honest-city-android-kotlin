package cz.city.honest.application.model

import android.app.Application
import cz.city.honest.application.android.service.AndroidServiceModule
import cz.city.honest.application.configuration.BaseApplication
import cz.city.honest.application.configuration.ContextModule
import cz.city.honest.application.job.JobModule
import cz.city.honest.application.model.gateway.GatewayModule
import cz.city.honest.application.model.property.PropertyModule
import cz.city.honest.application.model.repository.TestConfiguration
import cz.city.honest.application.model.repository.RepositoryModule
import cz.city.honest.application.model.service.ServiceModule
import cz.city.honest.application.view.ActivityModule
import cz.city.honest.application.viewmodel.ViewModelModule
import dagger.BindsInstance
import dagger.Component
import dagger.android.AndroidInjector
import dagger.android.DaggerApplication
import dagger.android.support.AndroidSupportInjectionModule
import javax.inject.Singleton

@Singleton
@Component(modules = [JobModule::class,ContextModule::class, AndroidSupportInjectionModule::class, AndroidServiceModule::class, ActivityModule::class, ServiceModule::class, GatewayModule::class, RepositoryModule::class, AndroidSupportInjectionModule::class, ViewModelModule::class, PropertyModule::class])
interface ModelConfiguration : AndroidInjector<DaggerApplication> {

    fun inject(application: BaseApplication): Unit;

    @Component.Builder
    interface Builder {
        @BindsInstance
        fun application(application: Application): Builder
        fun build(): ModelConfiguration
    }
}
