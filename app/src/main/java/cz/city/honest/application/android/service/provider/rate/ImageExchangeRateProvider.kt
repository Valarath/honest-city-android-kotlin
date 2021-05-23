package cz.city.honest.application.android.service.provider.rate

import androidx.lifecycle.MutableLiveData
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.Text
import com.google.mlkit.vision.text.TextRecognition
import cz.city.honest.application.model.dto.CurrencySettings
import cz.city.honest.application.model.service.settings.CurrencySettingsService
import cz.city.honest.application.viewmodel.postClearValue
import cz.city.honest.mobile.model.dto.ExchangeRate
import cz.city.honest.mobile.model.dto.ExchangeRateValues
import cz.city.honest.mobile.model.dto.Rate
import cz.city.honest.mobile.model.dto.Watched
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
            provide(it)
                .subscribe {
                    imageExchangeRateResultProvider.result.postClearValue(it)
                }
        }

    fun provide(text: Text) =
        currencySettingsService.get()
            .toList()
            .filter { containsAllCurrencies(it, text) }
            .map { toExchangeRate(text, it) }

    private fun containsAllCurrencies(
        currencySettings: MutableList<CurrencySettings>,
        text: Text
    ) = currencySettings.isNotEmpty() && currencySettings.all { containsCurrency(text, it) }

    private fun containsCurrency(
        text: Text,
        currencySettings: CurrencySettings
    ) = text.text
        .toLowerCase(Locale.ROOT)
        .contains(currencySettings.currency)


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

    private fun toRates(
        text: Text,
        currencySettings: MutableList<CurrencySettings>
    ): MutableSet<Rate> =
        currencySettings
            .map { toRate(text, it, getMainCurrency(currencySettings)) }
            .toMutableSet()

    private fun getMainCurrency(currencySettings: MutableList<CurrencySettings>) =
        currencySettings.first { it.mainCountryCurrency }

    private fun toRate(
        text: Text,
        currencySettings: CurrencySettings,
        mainCurrency: CurrencySettings
    ) =
        text.textBlocks
            .flatMap { it.lines }
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

class ImageExchangeRateResultProvider(val result: MutableLiveData<ExchangeRate> = MutableLiveData())