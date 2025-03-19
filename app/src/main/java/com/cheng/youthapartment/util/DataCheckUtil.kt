package com.cheng.youthapartment.util

import java.util.regex.Pattern

/**
 * 数据检查工具类
 * @author CHENG
 * @since 2025/3/19
 */
object DataCheckUtil {
    private const val PHONE_REGEX: String =
        "^(13[0-9]|15[012356789]|17[013678]|18[0-9]|14[57]|19[89]|166)[0-9]{8}"

    /**
     * 号码校验
     */
    fun checkPhone(phone: String): Boolean {
        if (phone.isEmpty()) return false
        val pattern = Pattern.compile(PHONE_REGEX)
        return pattern.matcher(phone).matches()
    }

    /**
     * 数据校验
     */
    fun checkEmpty(map: Map<String, String>): Boolean {
        for ((key, value) in map) {
            if (key.isEmpty()) {
                value.showToast()
                return false
            }
        }
        return true
    }


    /**
     * 数据提交校验
     */
    fun checkSubmit(map: Map<String, String>, phone: String): Boolean {
        for ((key, value) in map) {
            if (key.isEmpty()) {
                value.showToast()
                return false
            }
        }
        if (!checkPhone(phone)) {
            "号码格式有误".showToast()
            return false
        }
        return true
    }
}