package com.dudu.common.log

import com.dudu.common.BuildConfig
import com.dudu.common.R
import com.dudu.common.util.ContextManager
import com.dudu.common.util.FileUtil
import com.orhanobut.logger.FormatStrategy
import com.orhanobut.logger.Logger
import com.orhanobut.logger.PrettyFormatStrategy
import com.tencent.mars.xlog.Log
import com.tencent.mars.xlog.Xlog
import java.io.File


/**
 * 功能介绍
 * Created by Dzc on 2023/7/27.
 */
class CommonLogLoader : LogLoader {



    companion object{
        val defaultTag by lazy {
            ContextManager.getContext().resources.getString(R.string.log_tag_name)
        }

        // 各种Log参数配置初始化
        fun logInit(){
            loggerInit()
            initXLog()
        }

        // 输出到logcat日志初始
        private fun loggerInit(){
            val formatStrategy: FormatStrategy = PrettyFormatStrategy.newBuilder()
            //    .showThreadInfo(true) // 是否显示进程，默认true
                .methodCount(2) // 显示多少行执行方法栈，默认2行
                .methodOffset(2) // 隐藏内部方法调用（方法从下往上隐藏），默认0
            //    .logStrategy(customLog) // 日志输出策略， 默认 LogCat
                .tag(defaultTag) // 日志标签. Default PRETTY_LOGGER
                .build()
            // 根据BuildConfig.DEBUG，才显示日志到控制台
            Logger.addLogAdapter(LoggerAdapter(formatStrategy))
        }

        private fun initXLog(){
            System.loadLibrary("c++_shared")
            System.loadLibrary("marsxlog")

            val xLog = Xlog()
            Log.setLogImp(xLog)
            Log.setConsoleLogOpen(false)// 暂不使用xLog输出日志
            Log.appenderOpen(
                if(BuildConfig.DEBUG) Xlog.LEVEL_DEBUG else Xlog.LEVEL_INFO,
                Xlog.AppednerModeAsync, // 异步写入文件
                FileUtil.getCacheDir() + File.separator + BuildConfig.LOG_DIR,// mmap缓存路径
                FileUtil.getExternalFileDir() + File.separator + BuildConfig.LOG_DIR,
                "log", 0
            )

        }

//        private fun initLogan(){

            // 删除文件最大值 10M, 删除天数 7天，
//        var config = LoganConfig.Builder()
//            .setCachePath(FileUtil.getCacheDir() + File.separator + BuildConfig.LOG_DIR)// mmap缓存路径
//            .setPath(FileUtil.getExternalFileDir() + File.separator + BuildConfig.LOG_DIR)// 打包后文件路径
//            .setEncryptKey16("0123456789012345".toByteArray())// 加密
//            .setEncryptIV16("0123456789012345".toByteArray())
//            .build()
//        Logan.init(config)
            // Logan.setDebug(true)// Logan输出普通运行日志
            // 日志读写状态
            //Logan.setOnLoganProtocolStatus()
//        }

    }

    override fun d(msg: String) {
        Logger.d(msg)
    }

    override fun e(throwable: Throwable?, msg: String) {
        Logger.e(throwable, msg)

        Log.e(defaultTag, msg, throwable)
    }

    override fun w(msg: String) {
        Logger.w(msg)
    }

    override fun v(msg: String) {
        Logger.v(msg)
    }

    override fun i(msg: String) {
        Logger.i(msg)
    }

    override fun wtf(msg: String) {
        Logger.wtf(msg)
    }

    override fun x(msg: String) {
        Logger.i(msg)
        Log.i(defaultTag, msg)
    }


}