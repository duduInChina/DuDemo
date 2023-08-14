package com.dudu.log

import com.dudu.common.base.viewmodel.BaseViewModel
import com.dudu.common.ext.lifecycle
import com.dudu.common.util.ContextManager
import com.dudu.upload.UploadRepository
import java.io.File

/**
 * 功能介绍
 * Created by Dzc on 2023/8/7.
 */
class LogViewModel : BaseViewModel() {

    fun uploadXLog(file: File) {
        UploadRepository.uploadFile(
            "https://sales.fuxi.com/upload/image",
            file,
            mapOf("vipid" to "pda2444")
        ).lifecycle(this, loadingDialog = true) {
            ContextManager.showToast("上传成功")
        }
    }

}