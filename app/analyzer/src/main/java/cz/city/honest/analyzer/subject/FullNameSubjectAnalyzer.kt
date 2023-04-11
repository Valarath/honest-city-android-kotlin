package cz.city.honest.analyzer.subject

import cz.city.honest.service.settings.SubjectSettingsService
import io.reactivex.rxjava3.core.Maybe

class FullNameSubjectAnalyzer(private val subjectSettingsService: SubjectSettingsService): SubjectAnalyzer {

    override fun analyze(lines: List<String>): Maybe<String> = subjectSettingsService.get()
        .map {it.names  }
        .filter{containsExchangeName(lines,it) }
        .map { it.first() }

    private fun containsExchangeName(lines: List<String>, names: List<String>) =
        names.any {name ->
            lines.map { it.toUpperCase() }
                .any { it.contains(name) }
        }
}