package cz.city.honest.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.LiveDataReactiveStreams
import androidx.lifecycle.map
import cz.city.honest.dto.HonestyStatus
import javax.inject.Inject

class FilterViewModel @Inject constructor(private val filterService: cz.city.honest.service.filter.FilterService) :
    ScheduledObservableViewModel() {


    val filterData = LiveDataReactiveStreams.fromPublisher(getFilters().toFlowable())

    val honestyStatusVisibilityLiveData : LiveData<MutableMap<HonestyStatus, Boolean>>
        get() = filterData.map { it.subjectFilter.honestyStatusVisibilityMap }


    fun updateVisibilityState(status: HonestyStatus, filterValue:Boolean) =
        filterData.value
            ?.also { it.subjectFilter.honestyStatusVisibilityMap[status] = filterValue }
            ?.let { filterService.setFilter(it) }
            ?.subscribe()


    /**@Bindable
    fun getHonestyStatusVisibilityMap() = filterData.value
        ?.subjectFilter
        ?.honestyStatusVisibilityMap

    fun setHonestyStatusVisibilityMap(honestyStatusVisibilityMap: Map<HonestyStatus, Boolean>) {
        notifyPropertyChanged(BR.filterViewModel)
    }**/
    //fun onStatusFilterChange(state:HonestyStatus, value:Boolean) = honestyStatusVisibilityLiveData.

    private fun getFilters() = filterService.getFilter()

}
