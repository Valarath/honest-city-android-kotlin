package cz.city.honest.application.viewmodel

import androidx.databinding.PropertyChangeRegistry
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.schedulers.Schedulers
import java.util.concurrent.TimeUnit

open class ScheduledObservableViewModel : ViewModel(), androidx.databinding.Observable {

    private val callbacks: PropertyChangeRegistry = PropertyChangeRegistry()

    protected fun scheduleFlowable(
        initialDelay: Long = 0,
        period: Long = 10L,
        backpressureBuffer: Int = 1000
    ) = Flowable.interval(initialDelay, period, TimeUnit.SECONDS)
        .onBackpressureBuffer(backpressureBuffer)
        .subscribeOn(Schedulers.io())
        .observeOn(Schedulers.newThread())

    protected fun scheduleObservable(
        initialDelay: Long = 0,
        period: Long = 10L
    ) = Observable.interval(initialDelay, period, TimeUnit.SECONDS)
        .subscribeOn(Schedulers.io())
        .observeOn(Schedulers.newThread())

    override fun addOnPropertyChangedCallback(callback: androidx.databinding.Observable.OnPropertyChangedCallback) {
        callbacks.add(callback)
    }

    override fun removeOnPropertyChangedCallback(callback: androidx.databinding.Observable.OnPropertyChangedCallback) {
        callbacks.remove(callback)
    }

    protected fun notifyChange() {
        callbacks.notifyCallbacks(this, 0, null)
    }

    protected fun notifyPropertyChanged(fieldId: Int) {
        callbacks.notifyCallbacks(this, fieldId, null)
    }
}

//TODO presun podle vyskytu tam kam patri
fun <DATA> MutableLiveData<DATA>.postClearValue(value: DATA) = this.apply {
    postValue(null)
    postValue(value!!)
}
