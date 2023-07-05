package com.dudu.camera.util

import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.provider.MediaStore
import androidx.core.content.FileProvider
import com.dudu.camera.BuildConfig
import com.dudu.common.util.ContextManager
import com.dudu.common.util.FileUtil
import java.io.File

/**
 * 功能介绍
 * Created by Dzc on 2023/7/2.
 */
object CameraUtil {
    /**
     * 打开系统相机
     */
    fun getSysCameraIntent(outputUri: Uri): Intent {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        // 指定输出到文件uri中
        intent.putExtra(MediaStore.EXTRA_OUTPUT, outputUri)
        return intent
    }

    fun getCameraUri(fileName: String): Uri {
        var file = FileUtil.createNewFile(
            FileUtil.getAppLocalPath() + File.separator +
                    BuildConfig.CAMERA_FILE_SUFFIX + File.separator +
                    fileName
        )

        return FileProvider.getUriForFile(
            ContextManager.getContext(),
            "${ContextManager.getContext().packageName}${BuildConfig.CAMERA_AUTHORITIES_SUFFIX}",
            file
        )
    }

    /**
     * 图片裁剪，需使用公有文件夹Uri
     */
    fun getSysCrop(inputUri: Uri, outputUri: Uri): Intent{
        val intent = Intent("com.android.camera.action.CROP")
        intent.setDataAndType(inputUri, "image/*")
        //以下两行添加，解决无法加载此图片的提示
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
        intent.putExtra(MediaStore.EXTRA_OUTPUT, outputUri)
        intent.putExtra("crop", "true")
        intent.putExtra("aspectX", 1) // 裁剪框比例
        intent.putExtra("aspectY", 1)
        intent.putExtra("outputX", 100) // 输出图片大小
        intent.putExtra("outputY", 100)
        intent.putExtra("scale", false)
        intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString())
//        intent.putExtra("return-data", true)
        return intent
    }

    fun getCropUri(fileName: String): Uri {
        var file = FileUtil.createNewFile(
            FileUtil.getPublicPictureDirectory() + File.separator +
                    fileName
        )
        return Uri.fromFile(file)
    }
}