package cz.city.honest.job

import android.content.Context
import cz.city.honest.service.update.UpdateService
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class JobModule() {
    companion object {
        @Provides
        @Singleton
        fun provideUpdateWorkerManagerService(
            context: Context,
            workersFactory: UpdateWorkerFactory
        ) =
            UpdateWorkerManagerService(context, workersFactory)

        @Provides
        @Singleton
        fun provideUpdateWorkerFactory(updateService: UpdateService) =
            UpdateWorkerFactory(updateService)
    }
}
