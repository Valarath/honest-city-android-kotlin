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

class ImageExchangeRateProvider(
    private val currencySettingsService: CurrencySettingsService,
    private val imageExchangeRateResultProvider: ImageExchangeRateResultProvider
) {

    fun provide(image: InputImage) = TextRecognition
        .getClient()
        .process(image)
        .addOnSuccessListener {
            provide(it).subscribe { imageExchangeRateResultProvider.result.postClearValue(it) }
        }

    fun provide(text: Text) =
        currencySettingsService.get()
            .toList()
            .filter { containsAllCurrencies(it, text) }
            .map { toExchangeRate(text, it) }!!

    private fun containsAllCurrencies(
        currencySettings: MutableList<CurrencySettings>,
        text: Text
    ) = currencySettings.isNotEmpty()
            && currencySettings.all { containsCurrency(text, it) }
            && validateLines(text, currencySettings, getMainCurrency(currencySettings))

    private fun validateLines(
        text: Text,
        currencySettings: MutableList<CurrencySettings>,
        mainCurrency: CurrencySettings
    ) = currencySettings
        .filter { it != mainCurrency }
        .any { validateLines(text, it, mainCurrency) }

    private fun validateLines(
        text: Text,
        currencySettings: CurrencySettings,
        mainCurrency: CurrencySettings
    ) = text.textBlocks
        .flatMap { it.lines }
        .any { validateLine(it, currencySettings, mainCurrency) }

    private fun validateLine(
        line: Text.Line,
        currencySettings: CurrencySettings,
        mainCurrency: CurrencySettings
    ) = containsLineLanguage(line, currencySettings)
            && isDemand(line, mainCurrency, currencySettings)

    private fun containsCurrency(
        text: Text,
        currencySettings: CurrencySettings
    ) = text.text
        .toLowerCase(Locale.getDefault())
        .contains(currencySettings.currency)


    private fun toExchangeRate(
        text: Text,
        currencySettings: MutableList<CurrencySettings>
    ) = ExchangeRate(
        id = UUID.randomUUID().toString(),
        rates = toRates(text, currencySettings),
        watched = Watched(LocalDate.now(), null)
    )

    private fun toRates(
        text: Text,
        currencySettings: MutableList<CurrencySettings>
    ): MutableSet<Rate> =
        currencySettings
            .filter { !it.mainCountryCurrency }
            .map { toRate(text, it, getMainCurrency(currencySettings)) }
            .toMutableSet()

    private fun getMainCurrency(currencySettings: MutableList<CurrencySettings>) =
        currencySettings.first { it.mainCountryCurrency }

    private fun toRate(
        text: Text,
        currencySettings: CurrencySettings,
        mainCurrency: CurrencySettings
    ) = text.textBlocks.flatMap { it.lines }
        .filter { containsLineLanguage(it, currencySettings) }
        .filter { isDemand(it, mainCurrency, currencySettings) }
        .map { toRate(it, currencySettings.currency) }
        .minBy { it.rateValues.buy }!!


    private fun toRate(line: Text.Line, currency: String) =
        Rate(
            currency = currency,
            rateValues = ExchangeRateValues(
                getExchangeRateValue(line)
            )
        )

    private fun getExchangeRateValue(line: Text.Line) =
        line.elements
            .map { it.text.replace(",", ".") }
            .mapNotNull { it.toDoubleOrNull() }
            .max()!!

    private fun isDemand(
        it: Text.Line,
        mainCurrency: CurrencySettings,
        currencySettings: CurrencySettings
    ) = it.text.toLowerCase(Locale.getDefault())
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
        .contains(currencySettings.currency.toLowerCase(Locale.getDefault()))

    private fun containsPrice(
        currencyPart: String,
        mainCurrency: CurrencySettings
    ) = currencyPart
        .substringAfter(mainCurrency.currency)
        .contains(Regex(".*\\d.*\\d.*"))


    //TODO check if this is not contains in isDemand?
    private fun containsLineLanguage(
        it: Text.Line,
        currencySettings: CurrencySettings
    ) = it.text.toLowerCase(Locale.getDefault()).contains(currencySettings.currency)
}

class ImageExchangeRateResultProvider(val result: MutableLiveData<ExchangeRate> = MutableLiveData())