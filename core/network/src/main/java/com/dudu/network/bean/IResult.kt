package com.dudu.network.bean

/**
 * 功能介绍
 * Created by Dzc on 2023/5/11.
 */
interface IResult {

    /**
     * 该bean中用于判断的属性
     */
    fun getErrorCode(): String

    /**
     * 该bean中判断为成功的字段
     */
    fun isSuccess(): String

    /**
     * 错误信息
     */
    fun getErrorMessage(): String

}