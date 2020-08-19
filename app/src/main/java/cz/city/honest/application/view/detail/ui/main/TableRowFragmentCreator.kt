package cz.city.honest.application.view.detail.ui.main

import android.content.Context
import android.view.View
import android.widget.TableRow
import android.widget.TextView

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

        fun getCellTemplate(text: String, context: Context): TextView = TextView(context).apply {
            this.text = text
            this.gravity = View.TEXT_ALIGNMENT_GRAVITY
            this.width = 0
            this.height = 120
            this.layoutParams = getTableCellLayoutsParams()
        }

        fun getCellTemplate(text: String, initWeight: Float, context: Context): TextView =
            TextView(context).apply {
                this.text = text
                this.gravity = View.TEXT_ALIGNMENT_GRAVITY
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
                setMargins(0, 20, 0, 0)
            }
        }

        fun getTableCellLayoutsParams(initWeight: Float): TableRow.LayoutParams {
            return TableRow.LayoutParams(
                TableRow.LayoutParams.WRAP_CONTENT,
                TableRow.LayoutParams.WRAP_CONTENT,
                initWeight
            ).apply {
                setMargins(0, 20, 0, 0)
            }
        }
    }
}