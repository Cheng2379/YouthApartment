package com.cheng.youthapartment.bean.properties

/**
 * 租约状态
 * <p>
 * 1:签约待确认
 * 2:已签约
 * 3:已取消
 * 4:已到期
 * 5:退租待确认
 * 6:已退租
 * 7:续约待确认
 * <p>
 * @author CHENG
 * @since 2025/3/26
 */
enum class LeaseStatus(val value: Int) {

    SIGN_AWAIT_CONFIRM(1),

    SIGNED(2),

    CANCELED(3),

    EXPIRED(4),

    TERMINATION_AWAIT_CONFIRM(5),

    TERMINATED(6),

    RENEWAL_AWAIT_CONFIRM(7);

    companion object {
        fun fromValue(value: Int): LeaseStatus {
            return entries.firstOrNull {
                it.value == value
            } ?: throw IllegalArgumentException("Unknown LeaseStatus value: $value")
        }
    }
}