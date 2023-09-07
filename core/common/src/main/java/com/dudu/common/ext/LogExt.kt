package com.dudu.common.ext

import android.database.Cursor
import com.dudu.common.CommonConstant

/**
 * String输出log日志扩展函数
 * Created by Dzc on 2023/7/24.
 */

fun String.logD() = CommonConstant.logLoader.d(this)
fun String.logE() = CommonConstant.logLoader.e(null, this)
fun String.logW() = CommonConstant.logLoader.w(this)
fun String.logV() = CommonConstant.logLoader.v(this)
fun String.logI() = CommonConstant.logLoader.i(this)

fun String.logX() = CommonConstant.logLoader.x(this)

fun <T> List<T>.logD() = CommonConstant.logLoader.obj(this)

fun Cursor.logD() = CommonConstant.logLoader.obj(this)
