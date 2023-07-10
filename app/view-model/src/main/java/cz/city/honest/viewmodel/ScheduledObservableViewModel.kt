package cz.city.honest.viewmodel

import androidx.databinding.PropertyChangeRegistry
import androidx.lifecycle.ViewModel
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Flowable
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
        .observeOn(AndroidSchedulers.mainThread())

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
