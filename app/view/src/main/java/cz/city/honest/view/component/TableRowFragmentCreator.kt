package cz.city.honest.view.detail.ui.main

import android.content.Context
import android.view.Gravity
import android.widget.TableRow
import android.widget.TextView
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

class TableRowCreator {

    companion object {
        fun getCell(value: Double, context: Context): TextView =
            getCellTemplate(value.toString(), context)

        fun getCell(value: String, context: Context): TextView = getCellTemplate(value, context)

        fun getCell(value: Double, initWeight: Float, context: Context): TextView =
            getCellTemplate(value.toString(), initWeight, context)

        fun getCell(value: Int, initWeight: Float, context: Context): TextView =
            getCellTemplate(value.toString(), initWeight, context)

        fun getCell(value: String, initWeight: Float, context: Context): TextView =
            getCellTemplate(value, initWeight, context)

        fun getCell(value: Instant, initWeight: Float, context: Context): TextView =
            getCellTemplate(formatInstantAsString(value), initWeight, context)

        fun getCell(initWeight: Float, context: Context): TextView =
            getCellTemplate(null, initWeight, context)

        fun getCellTemplate(text: String, context: Context): TextView = TextView(context).apply {
            this.text = text
            this.gravity = Gravity.CENTER
            this.width = 0
            this.height = 120
            this.layoutParams = getTableCellLayoutsParams()
        }

        fun getCellTemplate(text: String?, initWeight: Float, context: Context): TextView =
            TextView(context).apply {
                this.text = text
                this.gravity = Gravity.CENTER
                this.width = 0
                this.height = 120
                this.layoutParams = getTableCellLayoutsParams(initWeight)
            }

        fun getTableCellLayoutsParams(): TableRow.LayoutParams {
            return TableRow.LayoutParams(
                TableRow.LayoutParams.WRAP_CONTENT,
                TableRow.LayoutParams.WRAP_CONTENT,
                1f
            ).apply {
                setMargins(0, 5, 0, 0)
            }
        }

        fun getTableCellLayoutsParams(initWeight: Float): TableRow.LayoutParams {
            return TableRow.LayoutParams(
                TableRow.LayoutParams.WRAP_CONTENT,
                TableRow.LayoutParams.WRAP_CONTENT,
                initWeight
            ).apply {
                setMargins(0, 5, 0, 0)
            }
        }

        fun getTableCellLayoutsParams(initWeight: Float, marginTop: Int, width: Int = TableRow.LayoutParams.MATCH_PARENT): TableRow.LayoutParams {
            return TableRow.LayoutParams(
                width,
                TableRow.LayoutParams.MATCH_PARENT,
                initWeight
            ).apply {
                setMargins(0, marginTop, 0, 0)
            }
        }

        private fun formatInstantAsString(value: Instant) = DateTimeFormatter.ofPattern("dd/MM/yyyy")
            .let { value.atZone(ZoneId.of("Europe/Prague")).toLocalDate().format(it) }
    }
}