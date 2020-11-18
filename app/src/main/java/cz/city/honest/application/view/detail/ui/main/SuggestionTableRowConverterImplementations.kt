package cz.city.honest.application.view.detail.ui.main

import android.content.Context
import android.view.View
import android.widget.TableLayout
import android.widget.TableRow
import cz.city.honest.application.model.dto.ClosedExchangePointSuggestion
import cz.city.honest.application.model.dto.ExchangeRateSuggestion
import cz.city.honest.application.model.dto.NewExchangePointSuggestion
import cz.city.honest.application.model.dto.State
import cz.city.honest.mobile.model.dto.Rate


class ClosedExchangePointSuggestionTableRowConverter :
    SuggestionTableRowConverter<ClosedExchangePointSuggestion>() {

    override fun convert(suggestion: ClosedExchangePointSuggestion, context: Context): TableRow =
        getGenericSuggestionInformationPanel(context, suggestion)
}

class ExchangeRateSuggestionTableRowConverter :
    SuggestionTableRowConverter<ExchangeRateSuggestion>() {

    override fun convert(suggestion: ExchangeRateSuggestion, context: Context): View =
        TableLayout(context).apply {
            layoutParams = TableLayout.LayoutParams(
                TableLayout.LayoutParams.MATCH_PARENT,
                TableLayout.LayoutParams.MATCH_PARENT
            )
            addView(getGenericSuggestionInformationPanel(context, suggestion))
            addView(getExchangeRatesTable(context, suggestion))
        }

    private fun getExchangeRatesTable(
        context: Context,
        suggestion: ExchangeRateSuggestion
    ) = TableLayout(context).apply {
        suggestion.suggestedExchangeRate.rates.forEach {
            addView(getExchangeRateRow(context, it, suggestion.state))
        }
    }

    private fun getExchangeRateRow(
        context: Context,
        it: Rate,
        state: State
    ) =
        TableRow(context).apply {
            setBackgroundColor(context.getColor(stateColor[state]!!))
            addView(TableRowCreator.getCell(it.rateValues.buy, 1f, context))
            addView(TableRowCreator.getCell(it.currency, 1f, context))
        }


}

class NewExchangePointSuggestionTableRowConverter :
    SuggestionTableRowConverter<NewExchangePointSuggestion>() {

    override fun convert(suggestion: NewExchangePointSuggestion, context: Context): TableRow =
        getGenericSuggestionInformationPanel(context, suggestion)

}