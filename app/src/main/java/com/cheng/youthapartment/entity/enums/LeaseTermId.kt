package com.cheng.youthapartment.entity.enums

/**
 * 租期id
 *
 * @author CHENG
 * @since 2025/3/28
 */
enum class LeaseTermId(val value: Int) {

    ONE(1),
    TWO(2),
    THREE(3),
    SIX(4),
    TWELVE(6);

    companion object {

        fun getIdByType(monthCount: String): Int {
            return when (monthCount) {
                "1月" -> {
                    ONE.value
                }

                "2月" -> {
                    TWO.value
                }

                "3月" -> {
                    THREE.value
                }

                "6月" -> {
                    SIX.value
                }

                "12月" -> {
                    TWELVE.value
                }

                else -> ONE.value
            }
        }

        fun fromValue(value: Int): LeaseTermId {
            return LeaseTermId.entries.firstOrNull {
                it.value == value
            } ?: throw IllegalArgumentException("Unknown LeaseTermId value: $value")
        }
    }
}