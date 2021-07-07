package cz.city.honest.application.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.core.Maybe
import io.reactivex.rxjava3.schedulers.Schedulers
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

open class ScheduledViewModel : ViewModel() {

    protected fun scheduleFlowable(
        initialDelay: Long = 0,
        period: Long = 10L,
        backpressureBuffer: Int = 1000
    ) = Flowable.interval(initialDelay, period, TimeUnit.SECONDS)
            .onBackpressureBuffer(backpressureBuffer)
            .subscribeOn(Schedulers.io())
            .observeOn(Schedulers.newThread())
}

fun <DATA> MutableLiveData<DATA>.postClearValue(value: DATA) = this.apply {
    postValue(null)
    postValue(value!!)
}
