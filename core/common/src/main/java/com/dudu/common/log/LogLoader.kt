package com.dudu.common.log

/**
 * 功能介绍
 * Created by Dzc on 2023/7/24.
 */
interface LogLoader {
    fun d(msg: String)
    fun e(throwable: Throwable?, msg: String)
    fun w(msg: String)
    fun v(msg: String)
    fun i(msg: String)
    fun wtf(msg: String)

    // 利用xLog记录到文件
    fun x(msg: String)

    fun obj(obj: Any)
}