package com.dudu.common.ext

import android.app.Activity
import android.content.Intent
import androidx.activity.ComponentActivity
import androidx.activity.result.contract.ActivityResultContracts

inline fun ComponentActivity.registerResult(
    isSuccessCheck: Int = Activity.RESULT_OK,// 检查成功才会回调
    crossinline callback: (resultCode: Int, data: Intent?) -> Unit
) = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
    if (result.resultCode == isSuccessCheck) {
        callback(result.resultCode, result.data)
    }
}