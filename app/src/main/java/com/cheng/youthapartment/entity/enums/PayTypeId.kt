package com.cheng.youthapartment.entity.enums

/**
 * 支付方式id
 *
 * @author CHENG
 * @since 2025/3/28
 */
enum class PayTypeId(val value: Int) {

    MONTH(6),
    QUARTERLY(7),
    HALF_A_YEAR(8),
    YEAR(9);

    companion object {

        fun getIdByType(payType: String): Int {
            return when (payType) {
                "月付" -> {
                    MONTH.value
                }

                "季付" -> {
                    QUARTERLY.value
                }

                "半年付" -> {
                    HALF_A_YEAR.value
                }

                "年付" -> {
                    YEAR.value
                }

                else -> MONTH.value
            }
        }

        fun fromValue(value: Int): PayTypeId {
            return PayTypeId.entries.firstOrNull {
                it.value == value
            } ?: throw IllegalArgumentException("Unknown PayTypeId value: $value")
        }
    }
}