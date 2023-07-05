package com.dudu.common.bean

import androidx.annotation.DrawableRes
import com.dudu.common.R

/**
 * 异常布局模式
 * Created by Dzc on 2023/6/25.
 */
sealed class FailedViewModel(val failedText: String, @DrawableRes val resId: Int, val isClick: Boolean = true) {
    object HiddenView : FailedViewModel("", 0) // 删除布局
    object DataError : FailedViewModel("出错了，请稍后重试", R.drawable.icon_data_error)
    object EmptyError : FailedViewModel("暂无数据", R.drawable.icon_empty_error)
    object NetworkError : FailedViewModel("请检查您的网络连接", R.drawable.icon_signal_wifi_off)
    object HttpError : FailedViewModel("服务异常", R.drawable.icon_link_off)
}
