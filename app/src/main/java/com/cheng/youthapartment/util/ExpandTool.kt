package com.cheng.youthapartment.util

import android.content.Intent
import android.os.Build
import android.os.Parcelable
import android.text.Editable
import android.text.Html
import android.text.Spanned
import android.text.TextWatcher
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import androidx.recyclerview.widget.RecyclerView
import com.cheng.youthapartment.App
import com.google.android.material.snackbar.Snackbar

/**
 * 拓展函数工具类
 *
 * @author Cheng
 * @since 2025/1/4
 */

fun String.showToast(duration: Int = Toast.LENGTH_SHORT) {
    Toast.makeText(App.mContext, this, duration).show()
}

fun Int.showToast(duration: Int = Toast.LENGTH_SHORT) {
    Toast.makeText(App.mContext, this, duration).show()
}

fun View.showSnackbar(
    text: String,
    actionString: String? = null,
    duration: Int = Snackbar.LENGTH_SHORT,
    block: (() -> Unit)? = null
) {
    val snackbar = Snackbar.make(this, text, duration)
    if (actionString != null && block != null) {
        snackbar.setAction(actionString) {
            block()
        }
    }
    snackbar.show()
}

fun View.showSnackbar(
    text: String,
    resourceId: Int? = null,
    duration: Int = Snackbar.LENGTH_SHORT,
    block: (() -> Unit)? = null
) {

    val snackbar = Snackbar.make(this, text, duration)
    if (resourceId != null && block != null) {
        snackbar.setAction(resourceId) {
            block()
        }
    }
    snackbar.show()
}

fun CharSequence.toHtml(): Spanned {
    return this.toString().toHtml()
}

fun String.toHtml(flags: Int = Html.FROM_HTML_MODE_COMPACT): Spanned {
    return Html.fromHtml(this, flags)
}

@Suppress("DEPRECATION")
inline fun <reified T : Parcelable> Intent.getYAParcelableExtra(
    key: String
): T? {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        getParcelableExtra(key, T::class.java)
    } else {
        @Suppress("DEPRECATION")
        getParcelableExtra(key) as? T
    }
}

fun EditText.textChangedListener(
    beforeBlock: ((s: CharSequence?, start: Int, count: Int, after: Int) -> Unit)? = null,
    afterBlock: ((s: Editable?) -> Unit)? = null,
    onTextChangedBlock: ((s: CharSequence?, start: Int, before: Int, count: Int) -> Unit)? = null
) {
    val textWatcher = object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            beforeBlock?.invoke(s, start, count, after)
        }

        override fun onTextChanged(
            charSequence: CharSequence?,
            start: Int,
            before: Int,
            count: Int
        ) {
            onTextChangedBlock?.invoke(charSequence, start, before, count)
        }

        override fun afterTextChanged(s: Editable?) {
            afterBlock?.invoke(s)
        }
    }
    // 添加监听器
    this.addTextChangedListener(textWatcher)
}

fun View.findTextViewById(id: Int): TextView = findViewById(id)

fun View.findButtonById(id: Int): Button = findViewById(id)

fun View.findEditTextById(id: Int): EditText = findViewById(id)

fun View.findImageViewById(id: Int): ImageView = findViewById(id)

fun View.findRecyclerViewById(id: Int): RecyclerView = findViewById(id)


