package cz.honest.city.internal.filter

import android.content.Context
import cz.city.honest.dto.Filter
import cz.city.honest.dto.HonestyStatus
import cz.city.honest.dto.SubjectFilter
import cz.city.honest.service.gateway.internal.InternalFilterGateway
import cz.honest.city.internal.SharedPreferencesProperties
import io.reactivex.rxjava3.core.Single

class FilterSharedPreferenceRepository(
    private val context: Context,
    private val sharedPreferencesProperties: SharedPreferencesProperties
) : InternalFilterGateway {

    override fun getFilter(): Single<Filter> =
        Single.just(getSharedPreferences())
            .map { loadFilter() }

    override fun persistFilter(filter: Filter): Single<Filter> =
        Single.just(filter)
            .map { persistFilterInSharedPreference(it) }

    private fun persistFilterInSharedPreference(filter: Filter) = filter
        .also { persistSubjectFilter(it.subjectFilter) }

    private fun persistSubjectFilter(subjectFilter: SubjectFilter) =
        subjectFilter.honestyStatusVisibilityMap.entries
            .forEach { persistVisibilityStatusSetting(it) }

    private fun persistVisibilityStatusSetting(setting: Map.Entry<HonestyStatus, Boolean>) =
        getSharedPreferences()
            .edit()
            .apply { putBoolean(setting.key.name, setting.value) }
            .apply()

    private fun loadFilter() = Filter(
        subjectFilter = loadSubjectFilter()
    )

    private fun loadSubjectFilter() = SubjectFilter(
        honestyStatusVisibilityMap = loadHonestyStatusFilter()
    )

    private fun loadHonestyStatusFilter() = HonestyStatus.values()
        .associateBy(
            keySelector = { it },
            valueTransform = { getSharedPreferences().getBoolean(it.name, true) }
        ).toMutableMap()

    private fun getSharedPreferences() =
        context.getSharedPreferences(sharedPreferencesProperties.repositoryName, Context.MODE_PRIVATE)

}