package com.cheng.youthapartment.view

import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import android.view.Gravity
import android.view.ViewGroup
import android.widget.GridLayout
import android.widget.TextView
import com.cheng.youthapartment.R

/**
 *
 * @author CHENG
 * @since 2025/3/1
 */
class YAGridLayout @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : GridLayout(context, attrs, defStyleAttr) {
    private var mStyle: GridLayoutStyle = GridLayoutStyle.ATTR_STYLE

    init {
        layoutParams = LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        columnCount = 2
    }

    fun setGridLayoutStyle(style: GridLayoutStyle) {
        mStyle = style
    }

    fun setData(dataList: List<String>) {
        removeAllViews()
        when (mStyle) {

            GridLayoutStyle.ATTR_STYLE -> {
                dataList.forEachIndexed { index: Int, value ->
                    addAttrText(index, value)
                }
            }

            GridLayoutStyle.OTHER_STYLE -> {
                dataList.forEach {  value ->
                    addTwoText(value)
                }
            }

        }
    }

    private fun addAttrText(index: Int, value: String) {
        TextView(context).apply {
            text = value
            textSize = 16f
            setTextColor(Color.parseColor("#2D3338"))
            layoutParams = LayoutParams().apply {
                setMargins(0, 8, 0, 8)
                height = LayoutParams.WRAP_CONTENT
                // 行间距
                rowSpec = spec(index / 2)
                // 列间距
                columnSpec = spec(
                    index % 2,
                    LEFT,
                    1f
                )
                setMargins(100, 10, 0, 10)
            }
            addView(this)
        }
    }

    // todo 后续优化
    private fun addTwoText(value: String) {
        TextView(context).apply {
            text = value
            textSize = 14f
            gravity = Gravity.CENTER
            setTextColor(Color.parseColor("#2D3338"))
            layoutParams = LayoutParams().apply {
                setMargins(0, 8, 0, 8)
                height = LayoutParams.WRAP_CONTENT
                setMargins(0, 10, 0, 10)
            }
            addView(this)
        }
        TextView(context).apply {
            text = value
            textSize = 14f
            gravity = Gravity.CENTER
            setTextColor(Color.parseColor("#2D3338"))
            layoutParams = LayoutParams().apply {
                setMargins(0, 8, 0, 8)
                height = LayoutParams.WRAP_CONTENT
                setMargins(0, 10, 0, 10)
            }
            addView(this)
        }

    }

}
