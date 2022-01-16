/**
 *
 * @description
 * @author zhanzijian
 * @date 2022/01/16 20:30
 */
package com.example.viewapplication

import android.content.res.Resources
import android.util.TypedValue

val Float.dp: Float
    get() = TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_DIP,
        this,
        Resources.getSystem().displayMetrics
    )
val Int.dp
    get() = this.toFloat().dp