package com.dudu.common.executor

import java.util.concurrent.LinkedBlockingQueue
import java.util.concurrent.ThreadPoolExecutor
import java.util.concurrent.TimeUnit

/**
 * 线程池提供管理
 * Created by Dzc on 2023/9/2.
 */
object ExecutorManager {

    private const val FUTURE_CORE_POOL_SIZE = 2 // 核心线程
    private const val FUTURE_MAXIMUM_POOL_SIZE = 4 // 最大创建线程数
    private const val FUTURE_KEEP_ALIVE_TIME = 60L // 过期时间秒

    val executorFuture by lazy {
        val executor = ThreadPoolExecutor(
            FUTURE_CORE_POOL_SIZE,
            FUTURE_MAXIMUM_POOL_SIZE,
            FUTURE_KEEP_ALIVE_TIME,
            TimeUnit.SECONDS,
            LinkedBlockingQueue()
        )
        executor.allowCoreThreadTimeOut(true) // 核心线程随FUTURE_KEEP_ALIVE_TIME过时
        executor
    }

}