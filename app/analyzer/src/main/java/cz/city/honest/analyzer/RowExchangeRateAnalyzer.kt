package cz.city.honest.analyzer

import cz.city.honest.dto.*
import cz.city.honest.service.settings.CurrencySettingsService
import io.reactivex.rxjava3.core.Maybe
import java.time.LocalDate
import java.util.*

class RowExchangeRateAnalyzer(private val currencySettingsService: CurrencySettingsService) :
    ExchangeRateAnalyzer {

    override fun analyze(lines: List<String>): Maybe<ExchangeRate> =
        currencySettingsService.get()
            .toList()
            .filter { containsAllCurrencies(it, lines) }
            .map { toExchangeRate(lines, it) }

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