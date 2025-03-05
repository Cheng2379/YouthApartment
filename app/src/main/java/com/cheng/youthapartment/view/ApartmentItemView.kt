package com.cheng.youthapartment.view

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.GridLayout
import com.cheng.youthapartment.R

/**
 *
 * @author CHENG
 * @since 2025/3/2
 */
class ApartmentItemView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : GridLayout(context, attrs, defStyleAttr) {

    init {
        val view: View = LayoutInflater.from(context).inflate(
            R.layout.item_apartment_item,
            this, false
        )
        addView(view)
    }

}