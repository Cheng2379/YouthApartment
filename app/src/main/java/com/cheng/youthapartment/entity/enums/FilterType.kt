package com.cheng.youthapartment.entity.enums

/**
 * 首页的筛选类型, 首页一共有四种，除了地区单独处理外，其余三种数据统一通过该枚举值判断处理
 * @author CHENG
 * @since 2025/4/22
 */
enum class FilterType {
    // 价格
    PRICE_TYPE,
    // 支付方式
    PAY_TYPE,
    // 排序方式
    SORT_TYPE
}