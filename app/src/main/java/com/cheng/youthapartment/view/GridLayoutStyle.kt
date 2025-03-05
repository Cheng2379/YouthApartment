package com.cheng.youthapartment.view

/**
 *
 * @author CHENG
 * @since 2025/3/1
 */
sealed class GridLayoutStyle {
    /**
     * 属性样式
     */
    data object ATTR_STYLE: GridLayoutStyle()

    /**
     * 费用明细、付款方式、可选租期
     */
    data object OTHER_STYLE: GridLayoutStyle()
}