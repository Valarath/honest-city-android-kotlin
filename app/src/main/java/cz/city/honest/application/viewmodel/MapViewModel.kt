package cz.city.honest.application.viewmodel

import androidx.lifecycle.MutableLiveData
import cz.city.honest.application.model.service.SubjectService
import cz.city.honest.mobile.model.dto.WatchedSubject
import javax.inject.Inject

class MapViewModel @Inject constructor(
    subjectService: SubjectService
) : ScheduledViewModel() {

    val watchedSubjects: MutableLiveData< List<WatchedSubject>> = MutableLiveData()

    init {
        schedule {
            subjectService.getSubjects().toList().toObservable().subscribe {
                watchedSubjects.postClearValue(it)
            }
        }
    }


}
