package cz.city.honest.application.view.component.rate

import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import android.view.View
import android.widget.LinearLayout
import android.widget.TableLayout
import android.widget.TableRow
import cz.city.honest.application.R
import cz.city.honest.application.model.dto.ExchangeRate
import cz.city.honest.application.model.dto.ExchangeRateValues
import cz.city.honest.application.view.detail.ui.main.TableRowCreator
import cz.city.honest.application.view.detail.ui.main.toMap
import java.math.BigDecimal
import java.math.RoundingMode
import java.util.*

class ExchangeRateTable: LinearLayout {

    constructor(context: Context, attributeSet: AttributeSet):super(context,attributeSet)

    constructor(context: Context, data:ExchangeRateTableData):super(context,null){
        showExchangePointRates(data)
    }

    fun showExchangePointRates(
        data:ExchangeRateTableData
    ) {
        inflate(context, R.layout.exchange_rate_table, this)
        val tableLayout = getTableLayout()
        val firstRow = tableLayout.findViewById<TableRow>(R.id.exchange_rate_holder_headers)
        tableLayout.removeAllViews()
        tableLayout.addView(firstRow)
        addRows(getRows(data), tableLayout)
    }

    private fun getRows(data: ExchangeRateTableData) =
        getRows(data.authorityRate.rates.toMap(), data.exchangePointRate.rates.toMap())

    private fun getTableLayout(): TableLayout =
        findViewById(R.id.exchange_rate_holder)

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
                this.addView(TableRowCreator.getCell(currency.toUpperCase(Locale.getDefault()), context!!))
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
        TableRowCreator.getCell("${BigDecimal(difference).setScale(2, RoundingMode.HALF_UP)} %", context!!)
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
        .times(100)
        .run { correctFormat(this) }

    private fun correctFormat(difference: Double): Double =
        if (difference != 0.0)
            difference * -1
        else
            difference

}

data class ExchangeRateTableData(
    val authorityRate: ExchangeRate,
    val exchangePointRate: ExchangeRate,
    val root: View
)

fun Map.Entry<String, Any>.getLowerCaseKey() = this.key.toLowerCase(Locale.getDefault())