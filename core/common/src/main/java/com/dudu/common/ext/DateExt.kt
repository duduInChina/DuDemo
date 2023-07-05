package com.dudu.common.ext

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

const val DATE_FORMAT = "yyyy-MM-dd HH:mm:ss"
/**
 * 字符串转化为时间
 */
fun String.date(format:String=DATE_FORMAT): Date {
    val dateFormat = SimpleDateFormat(format, Locale.getDefault())
    return dateFormat.parse(this)!!
}

fun String.utcdate(format:String=DATE_FORMAT): String{
    val dateFormat = SimpleDateFormat(format, Locale.getDefault())
    return dateFormat.parse(this)!!.format(format)
}

/**
 * 时间转化为字符串
 * @param format 转化格式
 * @return
 */
fun Date.format(format: String = DATE_FORMAT): String {
    val dateFormat = SimpleDateFormat(format, Locale.getDefault())
    return dateFormat.format(this)
}

fun Date.calendar(): Calendar {
    val calendar = Calendar.getInstance()
    calendar.time = this
    return calendar
}
