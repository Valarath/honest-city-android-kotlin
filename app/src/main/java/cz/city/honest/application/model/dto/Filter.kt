package cz.city.honest.application.model.dto

data class Filter(val subjectFilter: SubjectFilter)

data class SubjectFilter(val honestyStatusVisibilityMap: MutableMap<HonestyStatus, Boolean>)