package com.cheng.youthapartment.util

import android.widget.ImageView
import com.cheng.youthapartment.R
import java.util.regex.Pattern

/**
 * 数据检查工具类
 * @author CHENG
 * @since 2025/3/19
 */
object DataUtil {
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
    
    fun setFacility(text: CharSequence, imageView: ImageView) {
        when (text) {
            "空调" -> {
                imageView.setImageResource(R.drawable.svg_air_conditioner)
            }

            "洗衣机" -> {
                imageView.setImageResource(R.drawable.svg_washing_machine)
            }

            "冰箱" -> {
                imageView.setImageResource(R.drawable.svg_icebox)
            }

            "书桌" -> {
                imageView.setImageResource(R.drawable.svg_desk)
            }

            "WIFI" -> {
                imageView.setImageResource(R.drawable.svg_wifi)
            }

            "床" -> {
                imageView.setImageResource(R.drawable.svg_bed)
            }

            "沙发" -> {
                imageView.setImageResource(R.drawable.svg_sofa)
            }

            "微波炉" -> {
                imageView.setImageResource(R.drawable.svg_microwave_oven)
            }

            "油烟机" -> {
                imageView.setImageResource(R.drawable.svg_range_hood)
            }

            "热水器" -> {
                imageView.setImageResource(R.drawable.svg_water_heater)
            }

            "衣柜" -> {
                imageView.setImageResource(R.drawable.svg_closet)
            }

            "电视机" -> {
                imageView.setImageResource(R.drawable.svg_tv_set)
            }

            else -> {
                imageView.setImageResource(R.drawable.svg_position)
            }
        }
        
    }
}