package cz.city.honest.application.android.service.provider.rate

import androidx.lifecycle.MutableLiveData
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.Text
import com.google.mlkit.vision.text.TextRecognition
import cz.city.honest.application.model.dto.*
import cz.city.honest.application.model.service.settings.CurrencySettingsService
import cz.city.honest.application.viewmodel.postClearValue
import java.time.LocalDate
import java.util.*
import kotlin.math.absoluteValue

class ImageExchangeRateProvider(
    private val currencySettingsService: CurrencySettingsService,
    private val imageExchangeRateResultProvider: ImageExchangeRateResultProvider
) {

    fun provide(image: InputImage, textCallback: (lines: List<String>) -> Unit) =
        TextRecognition
            .getClient()
            .process(image)
            .addOnSuccessListener {
                val textLines = getTextLines(it)
                textCallback.invoke(textLines)
                provide(textLines).subscribe {
                    imageExchangeRateResultProvider.result.postClearValue(
                        it
                    )
                }
            }

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

    fun provide(lines: List<String>) =
        currencySettingsService.get()
            .toList()
            .filter { containsAllCurrencies(it, lines) }
            .map { toExchangeRate(lines, it) }!!

    private fun containsAllCurrencies(
        currencySettings: MutableList<CurrencySettings>,
        lines: List<String>
    ) = currencySettings.isNotEmpty()
            && currencySettings.all { containsCurrency(lines, it) }
            && validateLines(lines, currencySettings, getMainCurrency(currencySettings))

    private fun validateLines(
        lines: List<String>,
        currencySettings: MutableList<CurrencySettings>,
        mainCurrency: CurrencySettings
    ) = currencySettings
        .filter { it != mainCurrency }
        .any { validateLines(lines, it, mainCurrency) }

    private fun validateLines(
        lines: List<String>,
        currencySettings: CurrencySettings,
        mainCurrency: CurrencySettings
    ) = lines
        .any { validateLine(it, currencySettings, mainCurrency) }

    private fun validateLine(
        line: String,
        currencySettings: CurrencySettings,
        mainCurrency: CurrencySettings
    ) = containsLineLanguage(line, currencySettings)
            && isDemand(line, mainCurrency, currencySettings)

    private fun containsCurrency(
        lines: List<String>,
        currencySettings: CurrencySettings
    ) = lines
        .map { it.toUpperCase(Locale.getDefault()) }
        .any { it.contains(currencySettings.currency) }


    private fun toExchangeRate(
        lines: List<String>,
        currencySettings: MutableList<CurrencySettings>
    ) = ExchangeRate(
        id = UUID.randomUUID().toString(),
        rates = toRates(lines, currencySettings),
        watched = Watched(LocalDate.now(), null)
    )

    private fun toRates(
        lines: List<String>,
        currencySettings: MutableList<CurrencySettings>
    ): MutableSet<Rate> =
        currencySettings
            .filter { !it.mainCountryCurrency }
            .map { toRate(lines, it, getMainCurrency(currencySettings)) }
            .toMutableSet()

    private fun getMainCurrency(currencySettings: MutableList<CurrencySettings>) =
        currencySettings.first { it.mainCountryCurrency }

    private fun toRate(
        lines: List<String>,
        currencySettings: CurrencySettings,
        mainCurrency: CurrencySettings
    ) = lines
        .filter { containsLineLanguage(it, currencySettings) }
        .filter { isDemand(it, mainCurrency, currencySettings) }
        .map { toRate(it, currencySettings.currency) }
        .minBy { it.rateValues.buy }!!


    private fun toRate(line: String, currency: String) =
        Rate(
            currency = currency,
            rateValues = ExchangeRateValues(
                getExchangeRateValue(line)
            )
        )

    private fun getExchangeRateValue(line: String) =
        line.split(" ")
            .map { it.replace(",", ".") }
            .mapNotNull { it.toDoubleOrNull() }
            .max()!!

    private fun isDemand(
        it: String,
        mainCurrency: CurrencySettings,
        currencySettings: CurrencySettings
    ) = it.toUpperCase(Locale.getDefault())
        .run {
            containsCurrency(this, mainCurrency, currencySettings)
                    && containsPrice(this, mainCurrency)
        }

    private fun containsCurrency(
        currencyPart: String,
        mainCurrency: CurrencySettings,
        currencySettings: CurrencySettings
    ) = currencyPart
        .substringBefore(mainCurrency.currency)
        .contains(currencySettings.currency.toUpperCase(Locale.getDefault()))

    private fun containsPrice(
        currencyPart: String,
        mainCurrency: CurrencySettings
    ) = currencyPart
        .substringAfter(mainCurrency.currency)
        .contains(Regex(".*\\d.*\\d.*"))


    //TODO check if this is not contains in isDemand?
    private fun containsLineLanguage(
        it: String,
        currencySettings: CurrencySettings
    ) = it.toUpperCase(Locale.getDefault()).contains(currencySettings.currency)
}



class ImageExchangeRateResultProvider(val result: MutableLiveData<ExchangeRate> = MutableLiveData())