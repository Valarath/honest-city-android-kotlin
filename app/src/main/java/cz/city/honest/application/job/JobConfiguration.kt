package cz.city.honest.application.job

import dagger.Module
import dagger.android.ContributesAndroidInjector
import javax.inject.Scope

@Module
abstract class JobModule {

    @ServiceScoped
    @ContributesAndroidInjector
    internal abstract fun provideDbUpdatedJob(): UpdateScheduledJob

}

@Scope
@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.TYPE, AnnotationTarget.FUNCTION)
annotation class ServiceScoped
