package cz.city.honest.application.model

import android.app.Application
import cz.city.honest.analyzer.AnalyzerModule
import cz.honest.city.internal.AndroidServiceModule
import cz.city.honest.application.configuration.BaseApplication
import cz.city.honest.application.configuration.ContextModule
import cz.city.honest.job.JobModule
import cz.city.honest.external.ServerSourceModule
import cz.city.honest.property.PropertyModule
import cz.city.honest.repository.RepositoryModule
import cz.city.honest.service.ServiceModule
import cz.city.honest.application.view.ActivityModule
import cz.city.honest.viewmodel.ViewModelModule
import dagger.BindsInstance
import dagger.Component
import dagger.android.AndroidInjector
import dagger.android.DaggerApplication
import dagger.android.support.AndroidSupportInjectionModule
import javax.inject.Singleton

@Singleton
@Component(modules = [cz.city.honest.job.JobModule::class,ContextModule::class, AndroidSupportInjectionModule::class, cz.honest.city.internal.AndroidServiceModule::class, ActivityModule::class, cz.city.honest.service.ServiceModule::class, ServerSourceModule::class, cz.city.honest.repository.RepositoryModule::class, AndroidSupportInjectionModule::class, cz.city.honest.viewmodel.ViewModelModule::class, cz.city.honest.property.PropertyModule::class, cz.city.honest.analyzer.AnalyzerModule::class])
interface ModelConfiguration : AndroidInjector<DaggerApplication> {

    fun inject(application: BaseApplication): Unit;

    @Component.Builder
    interface Builder {
        @BindsInstance
        fun application(application: Application): Builder
        fun build(): ModelConfiguration
    }
}
