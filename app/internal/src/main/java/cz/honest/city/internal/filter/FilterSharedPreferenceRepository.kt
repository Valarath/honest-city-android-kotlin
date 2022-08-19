package cz.honest.city.internal.filter

import android.content.Context
import cz.city.honest.dto.Filter
import cz.city.honest.dto.HonestyStatus
import cz.city.honest.dto.SubjectFilter
import io.reactivex.rxjava3.core.Single

class FilterSharedPreferenceRepository ( private val context: Context):
    cz.city.honest.service.filter.FilterPersistenceHandler {

    override fun getFilter(): Single<Filter> =
        Single.just(getSharedPreferences())
            .map {loadFilter() }

    override fun persistFilter(filter: Filter): Single<Filter> =
        Single.just(filter)
            .map { persistFilterInSharedPreference(it) }

    private fun persistFilterInSharedPreference(filter: Filter) = filter
            .also { persistSubjectFilter(it.subjectFilter) }

    private fun persistSubjectFilter(subjectFilter: SubjectFilter) =
        subjectFilter.honestyStatusVisibilityMap.entries
            .forEach{persistVisibilityStatusSetting(it)}

    private fun persistVisibilityStatusSetting(setting: Map.Entry<HonestyStatus, Boolean>) =
        getSharedPreferences()
            .edit()
            .apply { putBoolean(setting.key.name,setting.value) }
            .apply()

    private fun loadFilter() = Filter(
        subjectFilter = loadSubjectFilter()
    )

    private fun loadSubjectFilter() = SubjectFilter(
        honestyStatusVisibilityMap = loadHonestyStatusFilter()
    )

    private fun loadHonestyStatusFilter() = HonestyStatus.values()
        .associateBy (
            keySelector = {it},
            valueTransform = {getSharedPreferences().getBoolean(it.name,true) }
        ).toMutableMap()

    //TODO HONEST_CITY_PREFERENCE to string.xml, property, etc.
    private fun getSharedPreferences() = context.getSharedPreferences("HONEST_CITY_PREFERENCES", Context.MODE_PRIVATE)

}