package cz.city.honest.application.android.service.provider.rate

import androidx.lifecycle.MutableLiveData
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.Text
import com.google.mlkit.vision.text.TextRecognition
import cz.city.honest.application.model.dto.CurrencySetting
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
        currencySettings: MutableList<CurrencySetting>,
        text: Text
    ) = currencySettings.isNotEmpty()
            && currencySettings.all { containsCurrency(text, it) }
            && validateLines(text,currencySettings,getMainCurrency(currencySettings))

    private fun validateLines(
        text: Text,
        currencySettings: MutableList<CurrencySetting>,
        mainCurrency: CurrencySetting
    ) = currencySettings
        .any {validateLines(text,it, mainCurrency)  }

    private fun validateLines(
        text: Text,
        currencySetting: CurrencySetting,
        mainCurrency: CurrencySetting
    ) = text.textBlocks
        .flatMap { it.lines }
        .any { validateLine(it, currencySetting, mainCurrency) }

    private fun validateLine(
        line: Text.Line,
        currencySetting: CurrencySetting,
        mainCurrency: CurrencySetting
    ) = containsLineLanguage(line, currencySetting)
            //TODO tohle jeste zkontroluj
            && isDemand(line, mainCurrency, currencySetting)

    private fun containsCurrency(
        text: Text,
        currencySetting: CurrencySetting
    ) = text.text
        .toLowerCase(Locale.ROOT)
        .contains(currencySetting.currency)


    private fun toExchangeRate(
        text: Text,
        currencySettings: MutableList<CurrencySetting>
    ) = ExchangeRate(
        id = UUID.randomUUID().toString(),
        rates = toRates(text, currencySettings),
        watched = Watched(LocalDate.now(), null)
    )

    private fun toRates(
        text: Text,
        currencySettings: MutableList<CurrencySetting>
    ): MutableSet<Rate> =
        currencySettings
            .map { toRate(text, it) }
            .toMutableSet()

    private fun getMainCurrency(currencySettings: MutableList<CurrencySetting>) =
        currencySettings.first { it.mainCountryCurrency }

    private fun toRate(
        text: Text,
        currencySetting: CurrencySetting
    ) = text.textBlocks
        .flatMap { it.lines }
        .map { toRate(it, currencySetting.currency) }
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
        mainCurrency: CurrencySetting,
        currencySetting: CurrencySetting
    ) = it.text
        .substringBefore(mainCurrency.currency)
        .contains(currencySetting.currency)

    private fun containsLineLanguage(
        it: Text.Line,
        currencySetting: CurrencySetting
    ) = it.text.toLowerCase().contains(currencySetting.currency)
}

class ImageExchangeRateResultProvider(val result: MutableLiveData<ExchangeRate> = MutableLiveData())