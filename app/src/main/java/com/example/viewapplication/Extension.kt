/**
 *
 * @description
 * @author zhanzijian
 * @date 2022/01/16 20:30
 */
package com.example.viewapplication

import android.content.Context
import android.content.res.Resources
import android.os.Build
import android.util.TypedValue
import androidx.core.view.ViewCompat
import java.lang.Exception

val Float.dp: Float
    get() = TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_DIP,
        this,
        Resources.getSystem().displayMetrics
    )
val Int.dp
    get() = this.toFloat().dp

val Float.sp: Float
    get() = TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_SP,
        this,
        Resources.getSystem().displayMetrics
    )

val Int.sp
    get() = this.toFloat().sp

fun getColorById(context: Context?,resId :Int): Int {
    if (context == null) {
        return 0
    }
    val res = context.resources ?: return 0
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        res.getColor(resId, context.theme)
    } else {
        res.getColor(resId)
    }
}