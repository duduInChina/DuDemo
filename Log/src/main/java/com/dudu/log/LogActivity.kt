package com.dudu.log

import android.os.Bundle
import com.dudu.common.base.activity.BaseActivity
import com.dudu.common.base.activity.BaseVMActivity
import com.dudu.common.base.annotation.Title
import com.dudu.common.base.annotation.TitleType
import com.dudu.common.crash.XCrashManager
import com.dudu.common.ext.logD
import com.dudu.common.ext.logE
import com.dudu.common.ext.logI
import com.dudu.common.ext.logV
import com.dudu.common.ext.logW
import com.dudu.common.ext.logX
import com.dudu.common.log.CommonLogLoader
import com.dudu.common.util.FileUtil
import com.dudu.log.databinding.ActivityLogBinding

/**
 * 功能介绍
 * Created by Dzc on 2023/7/24.
 */
@Title(title = "日志", titleType = TitleType.COLL)
class LogActivity : BaseVMActivity<ActivityLogBinding, LogViewModel>() {

    private val trackerInfo = """
            参考Tracker无埋点记录处理
            application.registerActivityLifecycleCallbacks，注册activity生命周期监听
            fragmentManager.registerFragmentLifecycleCallbacks，注册fragment生命周期监听，在上面获取到activity.create即可判断注册监听
            在生命周期中即可记录页面曝光时间，记录该条曝光日志到数据库
            无埋点监听View点击事件的两种方式：
            • 基于AOP监听onClick，@Aspect
            • 通过生命周期监听，获取根View，轮询子View，setAccessibilityDelegate，获得监听
            同样根据View的特征，记录日志到数据库
            Handler时间轮询，提交日志记录
        """.trimIndent()

    override fun initView() {
        bodyBinding.trackerInfo.text = trackerInfo

        bodyBinding {
            logD.setOnClickListener {
                "logD".logD()
            }
            logE.setOnClickListener {
                "logE".logE()
            }
            logW.setOnClickListener {
                "logW".logW()
            }
            logV.setOnClickListener {
                "logV".logV()
            }
            logI.setOnClickListener {
                "logI".logI()
            }
            logX.setOnClickListener {
                "logX".logX()
            }
            logXPush.setOnClickListener {
                // 获取日志列表
                val fileList = FileUtil.getFilesListInDirectory(CommonLogLoader.XLogDir)
                if (fileList.isNotEmpty()) {
                    val file = fileList[0]
                    if (file.isFile) {
                        // 文件提交上传
                        viewModel.uploadXLog(file)
                    }
                }

            }

            javaCrash.setOnClickListener {
                XCrashManager.testJavaCrash(false)
            }

            nativeCrash.setOnClickListener {
                XCrashManager.testNativeCrash(false)
            }

            anrCrash.setOnClickListener {
                XCrashManager.testAnrCrash()
            }
            sendCrash.setOnClickListener {
                XCrashManager.sendAllTombstones()
            }


        }
    }

    override fun initFlow() {
    }

}