package cz.city.honest.application.view.detail.ui.main

import android.view.View
import android.widget.TableRow
import android.widget.TextView
import dagger.android.support.DaggerAppCompatDialogFragment

open class TableRowFragment : DaggerAppCompatDialogFragment() {

    protected fun getCell(value: Double): TextView = getCellTemplate(value.toString())

    protected fun getCell(value: String): TextView = getCellTemplate(value)

    protected fun getCell(value: Double, initWeight: Float): TextView =
        getCellTemplate(value.toString(), initWeight)

    protected fun getCell(value: Int, initWeight: Float): TextView =
        getCellTemplate(value.toString(), initWeight)

    protected fun getCell(value: String, initWeight: Float): TextView =
        getCellTemplate(value, initWeight)

    protected fun getCellTemplate(text: String): TextView = TextView(activity).apply {
        this.text = text
        this.gravity = View.TEXT_ALIGNMENT_GRAVITY
        this.width = 0
        this.height = 120
        this.layoutParams = getTableCellLayoutsParams()
    }

    protected fun getCellTemplate(text: String, initWeight: Float): TextView =
        TextView(activity).apply {
            this.text = text
            this.gravity = View.TEXT_ALIGNMENT_GRAVITY
            this.width = 0
            this.height = 120
            this.layoutParams = getTableCellLayoutsParams(initWeight)
        }

    protected fun getTableCellLayoutsParams(): TableRow.LayoutParams {
        return TableRow.LayoutParams(
            TableRow.LayoutParams.WRAP_CONTENT,
            TableRow.LayoutParams.WRAP_CONTENT,
            1f
        ).apply {
            setMargins(0, 20, 0, 0)
        }
    }

    protected fun getTableCellLayoutsParams(initWeight: Float): TableRow.LayoutParams {
        return TableRow.LayoutParams(
            TableRow.LayoutParams.WRAP_CONTENT,
            TableRow.LayoutParams.WRAP_CONTENT,
            initWeight
        ).apply {
            setMargins(0, 20, 0, 0)
        }
    }

}