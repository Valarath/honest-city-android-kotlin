package cz.city.honest.application.android.service.provider

import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.Text
import com.google.mlkit.vision.text.TextRecognition
import cz.city.honest.application.model.dto.CurrencySettings
import cz.city.honest.application.model.service.settings.CurrencySettingsService
import cz.city.honest.mobile.model.dto.ExchangeRate
import cz.city.honest.mobile.model.dto.ExchangeRateValues
import cz.city.honest.mobile.model.dto.Rate
import cz.city.honest.mobile.model.dto.Watched
import io.reactivex.rxjava3.core.Flowable
import java.time.LocalDate
import java.util.*

class CameraExchangeRateProvider(
    val cameraResult: Flowable<ExchangeRate> = Flowable.empty(),
    private val currencySettingsService: CurrencySettingsService
) {

    fun provide(image: InputImage) = TextRecognition
        .getClient()
        .process(image)
        .addOnSuccessListener { provide(it) }

    fun provide(text: Text) =
        currencySettingsService.get()
            .toList()
            .filter { containsAllCurrencies(it, text) }
            .map { toExchangeRate(text, it) }

    private fun containsAllCurrencies(
        it: MutableList<CurrencySettings>,
        text: Text
    ) = it.map { text.text.contains(it.currency) }
        .reduce { valueOne, valueTwo -> valueOne == valueTwo }


    private fun toExchangeRate(
        text: Text,
        currencySettings: MutableList<CurrencySettings>
    ): ExchangeRate {
        return ExchangeRate(
            id = UUID.randomUUID().toString(),
            rates = toRates(text, currencySettings),
            watched = Watched(LocalDate.now(), null)
        )
    }

    fun toRates(text: Text, currencySettings: MutableList<CurrencySettings>): MutableSet<Rate> =
        currencySettings
            .map { toRate(text, it, getMainCurrency(currencySettings)) }
            .toMutableSet()

    fun getMainCurrency(currencySettings: MutableList<CurrencySettings>) =
        currencySettings.first { it.mainCountryCurrency }

    fun toRate(
        text: Text,
        currencySettings: CurrencySettings,
        mainCurrency: CurrencySettings
    ) =
        text.textBlocks
            .flatMap { it.lines }
            .filter { containsLineLanguage(it, currencySettings) }
            .filter { isDemand(it, mainCurrency, currencySettings) }
            .map { toRate(it,currencySettings.currency) }
            .minBy { it.rateValues.buy }!!


    fun toRate(line: Text.Line, currency: String) =
        Rate(
            currency = currency,
            rateValues = ExchangeRateValues(
                getExchangeRateValue(line)
            )
        )

    private fun getExchangeRateValue(line: Text.Line) =
        line.elements.filter { it.text.toIntOrNull() != null }
            .first()
            .text
            .toDouble()

    private fun isDemand(
        it: Text.Line,
        mainCurrency: CurrencySettings,
        currencySettings: CurrencySettings
    ) = it.text
        .substringBefore(mainCurrency.currency)
        .contains(currencySettings.currency)

    private fun containsLineLanguage(
        it: Text.Line,
        currencySettings: CurrencySettings
    ) = it.text.toLowerCase().contains(currencySettings.currency)
}