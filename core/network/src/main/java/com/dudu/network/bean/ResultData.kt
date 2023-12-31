package com.dudu.network.bean

import kotlinx.serialization.Serializable

/**
 * 通常指定的Base bean，解析相关ErrorCode状态
 * 当前ResponseDataConverter完成解析抛出异常
 * Created by Dzc on 2023/5/11.
 */
@Serializable
data class ResultData<T>(val errcode: Int, val message: String = "", val data: T? = null) :
    IResult {

    override fun getErrorCode() = errcode.toString()

    override fun isSuccess() = "0"

    override fun getErrorMessage() = message

}