package com.dudu.common.exception

/**
 * 功能介绍
 * Created by Dzc on 2023/5/11.
 */
class ResultException(val code: String, message: String) : Exception(message) {
}