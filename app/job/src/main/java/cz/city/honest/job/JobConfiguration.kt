package cz.city.honest.job

import cz.honest.city.internal.sync.SyncAdapter
import dagger.Module
import dagger.android.ContributesAndroidInjector
import javax.inject.Scope

@Module
abstract class JobModule {

    @ServiceScoped
    @ContributesAndroidInjector
    internal abstract fun provideDbUpdatedJob(): UpdateScheduledJob

    @ServiceScoped
    @ContributesAndroidInjector
    internal abstract fun provideSyncAdapter(): SyncAdapter

}

@Scope
@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.TYPE, AnnotationTarget.FUNCTION)
annotation class ServiceScoped
