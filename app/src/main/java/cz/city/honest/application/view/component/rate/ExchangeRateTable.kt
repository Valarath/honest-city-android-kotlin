package cz.city.honest.application.view.component.rate

import android.app.Activity
import android.content.Context
import android.graphics.Color
import android.view.View
import android.widget.LinearLayout
import android.widget.TableLayout
import android.widget.TableRow
import cz.city.honest.application.R
import cz.city.honest.application.view.detail.SubjectDetailActivity
import cz.city.honest.application.view.detail.ui.main.TableRowCreator
import cz.city.honest.application.view.detail.ui.main.toMap
import cz.city.honest.mobile.model.dto.ExchangePoint
import cz.city.honest.mobile.model.dto.ExchangeRate
import cz.city.honest.mobile.model.dto.ExchangeRateValues

class ExchangeRateTable(
    context: Context,
    private val authorityRate: ExchangeRate,
    private val root: View
) : LinearLayout(context) {

    init {
        showExchangePointRates(authorityRate, root)
    }

    private fun showExchangePointRates(
        it: ExchangeRate,
        root: View
    ) {
        val tableLayout = getTableLayout(root)
        val firstRow = tableLayout.findViewById<TableRow>(R.id.exchange_rate_holder_headers)
        tableLayout.removeAllViews()
        tableLayout.addView(firstRow)
        addRows(getRows(it.rates.toMap(), getExchangePointRate()), tableLayout)
    }

    private fun getExchangePointRate() =
        ((context as Activity)!!.intent.extras[SubjectDetailActivity.INTENT_SUBJECT] as ExchangePoint)
            .exchangePointRate
            .rates
            .toMap()

    private fun getTableLayout(root: View): TableLayout =
        root.findViewById(R.id.exchange_rate_holder)

    private fun addRows(
        tableRows: List<TableRow>,
        tableLayout: TableLayout
    ): Unit = tableRows
        .forEach { tableLayout.addView(it) }

    private fun getRows(
        authorityRates: Map<String, ExchangeRateValues>,
        exchangePointRates: Map<String, ExchangeRateValues>
    ): List<TableRow> =
        authorityRates.entries
            .filter { exchangePointRates[it.key] != null }
            .map { getRow(it.value, exchangePointRates[it.key]!!, it.key) }

    private fun getRow(
        authorityRate: ExchangeRateValues,
        exchangePointRate: ExchangeRateValues,
        currency: String
    ): TableRow =
        TableRow(context)
            .apply {
                this.addView(TableRowCreator.getCell(currency, context!!))
                this.addView(TableRowCreator.getCell(exchangePointRate.buy, context!!))
                this.addView(TableRowCreator.getCell(authorityRate.buy, context!!))
                this.addView(
                    getPercentageDifferenceFromAuthorityRate(
                        getPercentageDifference(
                            authorityRate,
                            exchangePointRate
                        )
                    )
                )
            }

    private fun getPercentageDifferenceFromAuthorityRate(difference: Double) =
        TableRowCreator.getCell(difference, context!!)
            .apply {
                if (difference > 0)
                    this.setTextColor(Color.GREEN)
                if (difference < 0)
                    this.setTextColor(Color.RED)
            }

    private fun getPercentageDifference(
        authorityRate: ExchangeRateValues,
        exchangePointRate: ExchangeRateValues
    ) = ((authorityRate.buy - exchangePointRate.buy) / authorityRate.buy)
        .run { correctFormat(this) }

    private fun correctFormat(difference: Double): Double =
        if (difference != 0.0)
            difference * -1
        else
            difference

}