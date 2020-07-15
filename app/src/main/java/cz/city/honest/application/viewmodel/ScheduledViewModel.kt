package cz.city.honest.application.viewmodel

import androidx.lifecycle.ViewModel
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

open class ScheduledViewModel : ViewModel() {

    protected fun schedule(schedule: () -> Unit) = Executors.newSingleThreadScheduledExecutor()
        .scheduleAtFixedRate(schedule, 0, 5, TimeUnit.MINUTES)
}