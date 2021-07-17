package cz.city.honest.application.view.component.suggestion

import android.content.Context
import android.graphics.drawable.GradientDrawable
import android.graphics.drawable.LayerDrawable
import android.view.View
import android.widget.TableLayout
import android.widget.TableRow
import cz.city.honest.application.R
import cz.city.honest.application.model.dto.*
import cz.city.honest.application.view.detail.ui.main.TableRowCreator


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
            addView(getExchangeRateRow(context, it))
        }
        background = getBackground(context, suggestion.state)
    }

    private fun getExchangeRateRow(
        context: Context,
        it: Rate
    ) =
        TableRow(context).apply {
            addView(TableRowCreator.getCell(it.rateValues.buy, 1f, context))
            addView(TableRowCreator.getCell(it.currency, 1f, context))
        }

    private fun getBackground(context: Context, state: State): LayerDrawable? {
        val background = context.getDrawable(R.drawable.suggestion_sub_table) as LayerDrawable
        val backgroundHolder =
            background.findDrawableByLayerId(R.id.suggestion_sub_table_background_holder) as GradientDrawable
        backgroundHolder.setStroke(10,context.getColor(stateColor[state]!!))
        return background
    }
}

class NewExchangePointSuggestionTableRowConverter :
    SuggestionTableRowConverter<NewExchangePointSuggestion>() {

    override fun convert(suggestion: NewExchangePointSuggestion, context: Context): TableRow =
        getGenericSuggestionInformationPanel(context, suggestion)

}