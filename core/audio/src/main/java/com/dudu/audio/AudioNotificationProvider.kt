package com.dudu.audio

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Bundle
import androidx.core.app.NotificationCompat
import androidx.core.graphics.drawable.IconCompat
import androidx.media3.common.C
import androidx.media3.common.Player
import androidx.media3.common.util.Assertions
import androidx.media3.common.util.UnstableApi
import androidx.media3.session.CommandButton
import androidx.media3.session.DefaultMediaNotificationProvider
import androidx.media3.session.MediaNotification
import androidx.media3.session.MediaSession
import androidx.media3.session.MediaStyleNotificationHelper.MediaStyle
import androidx.media3.session.SessionCommand
import com.dudu.common.util.ContextManager
import com.google.common.collect.ImmutableList
import java.util.Arrays

/**
 * 通知处理
 * Created by Dzc on 2023/8/28.
 */
class AudioNotification{

    companion object {
        const val CHANNEL_ID = "audio_channel_normal"
        const val AUDIO_NOTIFICATION_ID = 7

        private fun getNotificationManager(): NotificationManager {
            val notificationManager = ContextManager.getContext()
                .getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

            notificationManager.getNotificationChannel(CHANNEL_ID) ?: let {
                // 注册应用通知渠道： 渠道id 、 渠道名称 、 重要等级
                val channel =
                    NotificationChannel(CHANNEL_ID, "音频", NotificationManager.IMPORTANCE_LOW)
                notificationManager.createNotificationChannel(channel)
            }

            return notificationManager
        }

        @UnstableApi
        fun getNotification(mediaSession: MediaSession): Notification {
            getNotificationManager()
            val player = mediaSession.player
            val isPlay =
                player.playWhenReady && player.playbackState != Player.STATE_ENDED

            // 媒体元数据
            val mediaMetadata = player.mediaMetadata

            val builder = NotificationCompat.Builder(ContextManager.getContext(), CHANNEL_ID)
                .setSmallIcon(com.dudu.common.R.mipmap.ic_launcher)
                .setColorized(true)// 是否允许设置背景颜色，可根据音乐数据进行切换
                .setContentTitle(mediaMetadata.title) // 通知标题
                .setContentText(mediaMetadata.artist) // 作者
                .setSubText(mediaMetadata.albumTitle) // 专辑名称
                .setOnlyAlertOnce(true)// 每次只会提醒一次声音，不会重复提醒
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)// 手机任何状态都显示，锁屏
                .setOngoing(false)// true: 左右滑动不能取消通知


            // 额外属性，设置颜色等
            mediaMetadata.extras?.let { extra ->
//            val color = extra.getLong("xx", 0).toInt()
//            builder.color = color
//            builder.setContentIntent() // 点击事件
            }

            // action
//        builder.setDeleteIntent(
//            actionFactory.createMediaActionPendingIntent(mediaSession, Player.COMMAND_STOP.toLong())
//        )
            // 加入这个才有统一媒体通知
            val mediaStyle = MediaStyle(mediaSession)
            // 当前已经有播放操作按钮，暂时没清楚下面添加按钮的作用
//        mediaStyle.setShowActionsInCompactView(
//            * addNotificationActions(
//                mediaSession,
//                getMediaButtons(
//                    player.availableCommands,
//                    customLayout,
//                    isPlay
//                ),
//                builder, actionFactory
//            )
//        )
//        if (player.isCommandAvailable(Player.COMMAND_STOP)) {
//            mediaStyle.setCancelButtonIntent(
//                actionFactory.createMediaActionPendingIntent(mediaSession, Player.COMMAND_STOP.toLong())
//            )
//        }

            builder.setStyle(mediaStyle)
            return builder.build()
        }
    }

    // 添加按钮信息Command
    @UnstableApi
    private fun getMediaButtons(
        playerCommands: Player.Commands,
        customLayout: ImmutableList<CommandButton>,
        showPauseButton: Boolean
    ): ImmutableList<CommandButton> {
        val commandButtons = ImmutableList.Builder<CommandButton>()
        // 是否存在，上一个 或 前一个
        if (playerCommands.containsAny(
                Player.COMMAND_SEEK_TO_PREVIOUS,
                Player.COMMAND_SEEK_TO_PREVIOUS_MEDIA_ITEM
            )
        ) {
            // 添加上一个的标识按钮
            val commandButtonExtras = Bundle()
            commandButtonExtras.putInt(
                DefaultMediaNotificationProvider.COMMAND_KEY_COMPACT_VIEW_INDEX,
                C.INDEX_UNSET
            )
            commandButtons.add(
                CommandButton.Builder()
                    .setPlayerCommand(Player.COMMAND_SEEK_TO_PREVIOUS_MEDIA_ITEM)
                    .setIconResId(androidx.media3.session.R.drawable.media3_notification_seek_to_previous)
                    .setDisplayName(
                        "前一首"
                    )
                    .setExtras(commandButtonExtras)
                    .build()
            )
        }
        // 是否存在播放按钮
        if (playerCommands.contains(Player.COMMAND_PLAY_PAUSE)) {
            // 添加播放按钮，判断当前播放状态
            val commandButtonExtras = Bundle()
            commandButtonExtras.putInt(
                DefaultMediaNotificationProvider.COMMAND_KEY_COMPACT_VIEW_INDEX,
                C.INDEX_UNSET
            )
            commandButtons.add(
                CommandButton.Builder()
                    .setPlayerCommand(Player.COMMAND_PLAY_PAUSE)
                    .setIconResId(
                        if (showPauseButton) androidx.media3.session.R.drawable.media3_notification_pause
                        else androidx.media3.session.R.drawable.media3_notification_play
                    )
                    .setExtras(commandButtonExtras)
                    .setDisplayName(
                        if (showPauseButton) "暂停"
                        else "播放"
                    )
                    .build()
            )
        }
        // 下一首
        if (playerCommands.containsAny(Player.COMMAND_STOP)) {
            val commandButtonExtras = Bundle()
            commandButtons.add(
                CommandButton.Builder()
                    .setPlayerCommand(Player.COMMAND_STOP)
                    .setIconResId(androidx.media3.session.R.drawable.media3_notification_seek_to_next)
                    .setExtras(commandButtonExtras)
                    .setDisplayName("下一首")
                    .build()
            )
        }
        // 添加自定义按钮
        for (i in customLayout.indices) {
            val button = customLayout[i]
            if (button.sessionCommand != null &&
                button.sessionCommand!!.commandCode == SessionCommand.COMMAND_CODE_CUSTOM
            ) {
                commandButtons.add(button)
            }
        }
        return commandButtons.build()
    }

    // 添加action到builder
    @UnstableApi
    private fun addNotificationActions(
        mediaSession: MediaSession,
        mediaButtons: ImmutableList<CommandButton>,
        builder: NotificationCompat.Builder,
        actionFactory: MediaNotification.ActionFactory
    ): IntArray {
        var compactViewIndices = IntArray(3)
        val defaultCompactViewIndices = IntArray(3)
        Arrays.fill(compactViewIndices, C.INDEX_UNSET)
        Arrays.fill(defaultCompactViewIndices, C.INDEX_UNSET)
        var compactViewCommandCount = 0
        for (i in mediaButtons.indices) {
            val commandButton = mediaButtons[i]
            if (commandButton.sessionCommand != null) {
                builder.addAction(
                    actionFactory.createCustomActionFromCustomCommandButton(
                        mediaSession,
                        commandButton
                    )
                )
            } else {
                Assertions.checkState(commandButton.playerCommand != Player.COMMAND_INVALID)
                builder.addAction(
                    actionFactory.createMediaAction(
                        mediaSession,
                        IconCompat.createWithResource(ContextManager.getContext(), commandButton.iconResId),
                        commandButton.displayName,
                        commandButton.playerCommand
                    )
                )
            }
            if (compactViewCommandCount == 3) {
                continue
            }
            val compactViewIndex = commandButton.extras.getInt(
                DefaultMediaNotificationProvider.COMMAND_KEY_COMPACT_VIEW_INDEX,
                C.INDEX_UNSET
            )
            if (compactViewIndex >= 0 && compactViewIndex < compactViewIndices.size) {
                compactViewCommandCount++
                compactViewIndices[compactViewIndex] = i
            } else if (commandButton.playerCommand == Player.COMMAND_SEEK_TO_PREVIOUS ||
                commandButton.playerCommand == Player.COMMAND_SEEK_TO_PREVIOUS_MEDIA_ITEM
            ) {
                defaultCompactViewIndices[0] = i
            } else if (commandButton.playerCommand == Player.COMMAND_PLAY_PAUSE) {
                defaultCompactViewIndices[1] = i
            } else if (commandButton.playerCommand == Player.COMMAND_SEEK_TO_NEXT ||
                commandButton.playerCommand == Player.COMMAND_SEEK_TO_NEXT_MEDIA_ITEM
            ) {
                defaultCompactViewIndices[2] = i
            }
        }
        if (compactViewCommandCount == 0) {
            // If there is no custom configuration we use the seekPrev (if any), play/pause (if any),
            // seekNext (if any) action in compact view.
            var indexInCompactViewIndices = 0
            for (i in defaultCompactViewIndices.indices) {
                if (defaultCompactViewIndices[i] == C.INDEX_UNSET) {
                    continue
                }
                compactViewIndices[indexInCompactViewIndices] = defaultCompactViewIndices[i]
                indexInCompactViewIndices++
            }
        }
        for (i in compactViewIndices.indices) {
            if (compactViewIndices[i] == C.INDEX_UNSET) {
                compactViewIndices = compactViewIndices.copyOf(i)
                break
            }
        }
        return compactViewIndices
    }
}

// 官方不稳定标志，后续查看是否安全（正式使用如不安全则改为标准创建通知方案）
@UnstableApi
class AudioNotificationProvider : MediaNotification.Provider {

    override fun createNotification(
        mediaSession: MediaSession,
        customLayout: ImmutableList<CommandButton>,
        actionFactory: MediaNotification.ActionFactory,
        onNotificationChangedCallback: MediaNotification.Provider.Callback
    ): MediaNotification {

        return MediaNotification(AudioNotification.AUDIO_NOTIFICATION_ID, AudioNotification.getNotification(mediaSession))
    }

    override fun handleCustomCommand(
        session: MediaSession,
        action: String,
        extras: Bundle
    ): Boolean {
        // 自定义命令，暂不支持
        return false
    }

}