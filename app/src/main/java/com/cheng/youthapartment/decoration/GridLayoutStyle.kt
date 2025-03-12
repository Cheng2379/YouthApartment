package com.cheng.youthapartment.decoration

/**
 *
 * @author CHENG
 * @since 2025/3/1
 */
sealed class GridLayoutStyle {
    /**
     * List数据填充整个GridLayout, 通过取模方式, 每两个数左右对齐
     * 例如：属性样式
     */
    data object ATTR_STYLE: GridLayoutStyle()

    /**
     * Map数据填充GridLayout, key 和 value 分别左右对齐
     * 例如：用明细、付款方式、可选租期
     */
    data object OTHER_STYLE: GridLayoutStyle()
}