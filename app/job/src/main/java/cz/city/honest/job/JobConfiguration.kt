package cz.city.honest.job

import android.content.Context
import cz.city.honest.service.provider.PropertyProvider
import cz.city.honest.service.update.UpdateService
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class JobModule() {
    companion object {
        @Provides
        @Singleton
        fun getUpdateJobProperties(propertyProvider: PropertyProvider) =
            propertyProvider.providePropertyOfType(UpdateJobProperties::class.java)

        @Provides
        @Singleton
        fun provideUpdateWorkerManagerService(
            context: Context,
            workersFactory: UpdateWorkerFactory,
            updateJobProperties: UpdateJobProperties
        ) =
            UpdateWorkerManagerService(context, workersFactory,updateJobProperties)

        @Provides
        @Singleton
        fun provideUpdateWorkerFactory(updateService: UpdateService) =
            UpdateWorkerFactory(updateService)
    }
}

data class UpdateJobProperties(var repeatInterval: Long, var initialDelay:Long){
    constructor():this(0,0)
}
