package cz.city.honest.application.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

open class ScheduledViewModel : ViewModel() {

    protected fun schedule(schedule: () -> Unit) = Executors.newSingleThreadScheduledExecutor()
        .scheduleAtFixedRate(schedule, 0, 25, TimeUnit.MINUTES)
}

fun <DATA>MutableLiveData<DATA>.postClearValue(value: DATA) = this.apply {
    postValue(null)
    postValue(value!!)
}

class SingleTimeEventObserver<DATA>:Observer<DATA>{

    override fun onChanged(data: DATA?) {
        TODO("Not yet implemented")
    }
}