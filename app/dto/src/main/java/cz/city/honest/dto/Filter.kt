package cz.city.honest.dto

data class Filter(val subjectFilter: SubjectFilter)

data class SubjectFilter(val honestyStatusVisibilityMap: MutableMap<HonestyStatus, Boolean>)