package com.cheng.youthapartment.decoration.grid_view

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView

/**
 *
 * @author CHENG
 * @since 2025/3/13
 */
class LabelSpaceDecoration(
    private val spanCount: Int = 3,
    private val leftSpacing: Int = 0,
    private val topSpacing: Int = 0,
    private val rightSpacing: Int = 0,
    private val bottomSpacing: Int = 0
) : RecyclerView.ItemDecoration() {

    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        val position = parent.getChildAdapterPosition(view)
        // 只在标签之间添加右间距和下间距
        if (position % spanCount != spanCount - 1) {
            outRect.left = leftSpacing
            outRect.right = rightSpacing
        }
        // 添加下间距
        outRect.top = topSpacing
        outRect.bottom = bottomSpacing
    }
}