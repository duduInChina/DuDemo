package com.dudu.audio

import android.content.ComponentName
import androidx.media3.session.MediaBrowser
import androidx.media3.session.SessionToken
import com.dudu.common.util.ContextManager
import com.google.common.util.concurrent.ListenableFuture
import com.google.common.util.concurrent.MoreExecutors

/**
 * 媒体客户端，参与browser和service的连接
 * onConnected 成功连接回调
 * Created by Dzc on 2023/8/30.
 */
class AudioServiceConnection(val onConnected: (MediaBrowser) -> Unit) {

    private val mContext = ContextManager.getContext()

    private var browserFuture: ListenableFuture<MediaBrowser>? = null
    val browser: MediaBrowser?
        get() = browserFuture?.get()

    // 初始化，通常在生命周期开始时调用
    fun onStart() {
        if (browserFuture == null) {

            browserFuture = MediaBrowser.Builder(
                mContext,
                SessionToken(mContext, ComponentName(mContext, AudioService::class.java))
            ).buildAsync()

            browserFuture?.addListener({
                browser?.let {
                    onConnected(it)
                }
            }, MoreExecutors.directExecutor())
        }
    }

    // 退出连接，释放资源
    fun onStop() {
        browserFuture?.let {
            MediaBrowser.releaseFuture(it)
            browserFuture = null
        }
    }

}