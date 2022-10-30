package cz.honest.city.internal.provider.rate

import androidx.lifecycle.MutableLiveData
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.Text
import com.google.mlkit.vision.text.TextRecognition
import cz.city.honest.analyzer.ExchangeRateAnalyzer
import cz.city.honest.dto.ExchangeRate
import io.reactivex.rxjava3.core.Maybe
import io.reactivex.rxjava3.core.Observable
import kotlin.math.absoluteValue

class ImageExchangeRateProvider(
    private val imageExchangeRateResultProvider: ImageExchangeRateResultProvider,
    private val exchangeRateAnalyzers: List<ExchangeRateAnalyzer>,
    private var result:Observable<ExchangeRate> = Observable.empty()
) {

    fun provide(image: InputImage, textCallback: (lines: List<String>) -> Unit) =
        TextRecognition
            .getClient()
            .process(image)
            .addOnSuccessListener {
                val textLines = getTextLines(it)
                textCallback.invoke(textLines)
                Maybe.mergeArray(*exchangeRateAnalyzers.map { it.analyze(textLines) }
                    .toTypedArray())
                    .subscribe {
                        imageExchangeRateResultProvider.result.postClearValue(it)
                        result = Observable.just(it)
                    }
            }

    fun getResult() = result

    private fun getTextLines(text: Text) = mutableListOf<MutableList<Text.Line>>()
        .apply { fillTextLines(text, this) }
        .onEach { it.sortBy { it.cornerPoints!!.first().x } }
        .map { it.joinToString(" ") { it.text } }
        .map { it.replace("|", " ") }
        .map { it.replace("\\s+".toRegex(), " ").trim() }
        .map { removeDuplicities(it) }
        .toMutableList()
        .apply { this.removeIf { it.size != 4 } }
        .map { it.joinToString(" ") }

    private fun removeDuplicities(it: String) = it.split(" ")
        .toMutableList()
        .also { it.removeAll(getDuplicities(it)) }

    private fun getDuplicities(line: List<String>) = line
        .groupingBy { it.replace(",", "") }
        .eachCount()
        .filterValues { it > 1 }
        .keys


    private fun fillTextLines(text: Text, lines: MutableList<MutableList<Text.Line>>) = text
        .textBlocks
        .flatMap { it.lines }
        .sortedBy { it.cornerPoints!!.first().y }
        .filterIndexed { index, _ -> index >= getLinesSize(lines) }
        .forEach { addLine(lines, it) }

    private fun addLine(
        lines: MutableList<MutableList<Text.Line>>,
        it: Text.Line
    ) = if (isOnRow(lines, it))
        lines.last().add(it)
    else
        lines.add(mutableListOf(it))

    //TODO misto odcitani tam dej nejakej pomer at to zohledni vzdalenosti kamery od plochy
    private fun isOnRow(
        lines: MutableList<MutableList<Text.Line>>,
        line: Text.Line
    ) = lines.firstOrNull() != null
            && lines.first().firstOrNull() != null
            && (line.cornerPoints!!.first().y - lines.last()
        .first().cornerPoints!!.first().y).absoluteValue <= 10


    private fun getLinesSize(lines: List<List<Text.Line>>) = lines.flatten().count()

}

fun <DATA> MutableLiveData<DATA>.postClearValue(value: DATA) = this.apply {
    postValue(null)
    postValue(value!!)
}

class ImageExchangeRateResultProvider(val result: MutableLiveData<ExchangeRate> = MutableLiveData())