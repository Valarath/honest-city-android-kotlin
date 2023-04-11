package cz.city.honest.analyzer.subject

import io.reactivex.rxjava3.core.Maybe

interface SubjectAnalyzer {

    fun analyze(lines: List<String>): Maybe<String>
}