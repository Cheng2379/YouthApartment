package com.cheng.youthapartment.adapter

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.PorterDuff
import android.graphics.PorterDuffXfermode
import android.graphics.RectF
import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool
import com.bumptech.glide.load.resource.bitmap.BitmapTransformation
import java.nio.charset.Charset
import java.security.MessageDigest

/**
 * 设置图片样式
 *
 * @author Cheng
 * @since 2024/12/21
 */
class SquareCrop(private val radius: Int) : BitmapTransformation() {

    override fun updateDiskCacheKey(messageDigest: MessageDigest) {
        messageDigest.update("squareCropWithRoundedCorners".toByteArray(Charset.forName("UTF-8")))
    }

    override fun transform(pool: BitmapPool, toTransform: Bitmap, outWidth: Int, outHeight: Int): Bitmap {
        // 计算裁剪正方形的区域
        val size = Math.min(toTransform.width, toTransform.height)
        val x = (toTransform.width - size) / 2
        val y = (toTransform.height - size) / 2

        // 从中心裁剪出正方形部分
        val cropped = Bitmap.createBitmap(toTransform, x, y, size, size)

        // 创建一个带圆角的 Bitmap（圆角半径为radius）
        val roundedBitmap = Bitmap.createBitmap(cropped.width, cropped.height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(roundedBitmap)
        val paint = Paint(Paint.ANTI_ALIAS_FLAG)
        val rect = RectF(0f, 0f, cropped.width.toFloat(), cropped.height.toFloat())
        val roundRect = RectF(0f, 0f, cropped.width.toFloat(), cropped.height.toFloat())
        paint.color = Color.BLACK

        // 为Bitmap绘制圆角矩形
        canvas.drawRoundRect(roundRect, radius.toFloat(), radius.toFloat(), paint)
        paint.xfermode = PorterDuffXfermode(PorterDuff.Mode.SRC_IN)
        canvas.drawBitmap(cropped, 0f, 0f, paint)

        return roundedBitmap
    }
}

