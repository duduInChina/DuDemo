package com.dudu.audio

import android.Manifest
import android.content.Intent
import android.os.Build
import android.widget.SeekBar
import android.widget.SeekBar.OnSeekBarChangeListener
import androidx.media3.common.util.UnstableApi
import com.dudu.common.base.activity.BaseVMActivity
import com.dudu.common.base.annotation.Title
import com.dudu.common.base.annotation.TitleType
import com.dudu.common.widget.CustomBottomSheetDialog
import com.dudu.demoaudio.BuildConfig
import com.dudu.demoaudio.databinding.ActivityAudioBinding
import com.permissionx.guolindev.PermissionX
import com.dudu.demoaudio.R

/**
 * 功能介绍
 * Created by Dzc on 2023/8/30.
 */
@Title(title = "音频", true, TitleType.COLL)
class AudioActivity : BaseVMActivity<ActivityAudioBinding, AudioViewModel>() {

    private val localFragment: AudioLocalListFragment by lazy {
        AudioLocalListFragment(AudioListType.Local)
    }

    private val localBottomSheetDialog: CustomBottomSheetDialog by lazy {
        CustomBottomSheetDialog(localFragment)
    }


    private val currentFragment: AudioLocalListFragment by lazy {
        AudioLocalListFragment(AudioListType.Current)
    }

    private val currentBottomSheetDialog: CustomBottomSheetDialog by lazy {
        CustomBottomSheetDialog(currentFragment)
    }

    private val urlFragment: AudioLocalListFragment by lazy {
        AudioLocalListFragment(AudioListType.Url)
    }

    private val urlBottomSheetDialog: CustomBottomSheetDialog by lazy {
        CustomBottomSheetDialog(urlFragment)
    }

    private var soundManager: SoundManager? = null

    override fun onStart() {
        super.onStart()
    }

    override fun onDestroy() {
        AudioManager.release()
        soundManager?.release()
        super.onDestroy()
    }

    @UnstableApi
    override fun initView() {
        bodyBinding {
            seekBar.setOnSeekBarChangeListener(object : OnSeekBarChangeListener {
                override fun onProgressChanged(p0: SeekBar?, p1: Int, p2: Boolean) {
                }

                override fun onStartTrackingTouch(p0: SeekBar?) {
                }

                override fun onStopTrackingTouch(seekBar: SeekBar?) {
                    seekBar?.let {
                        AudioManager.seekTo(it.progress.toLong())
                    }
                }

            })

            playBtn.setOnClickListener {
                AudioManager.togglePlay()
            }

            previousBtn.setOnClickListener {
                AudioManager.previous()
            }

            nextBtn.setOnClickListener {
                AudioManager.next()
            }

            autoLocalBtn.setOnClickListener {
//                sheetBehavior.state = STATE_EXPANDED

                PermissionX.init(this@AudioActivity)
                    .permissions(
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU)
                            Manifest.permission.READ_MEDIA_AUDIO
                        else
                            Manifest.permission.READ_EXTERNAL_STORAGE
                    )
                    .request { allGranted, _, _ ->
                        if (allGranted) {
                            localBottomSheetDialog.show(supportFragmentManager, null)
                        }
                    }

            }

            urlBtn.setOnClickListener {
                urlBottomSheetDialog.show(supportFragmentManager, null)
            }

            playListBtn.setOnClickListener {
                currentBottomSheetDialog.show(supportFragmentManager, null)
            }

            soundLayout.setOnClickListener {
                AudioManager.volumeDelayed(1500)
                soundManager?.play(R.raw.sound_test1)
            }

            appWidgetLayout.setOnClickListener {

                sendBroadcast(Intent(BuildConfig.AUDIO_WIDGET_UPDATE).setPackage(packageName))
            }
        }

        soundManager ?: let {

            soundManager = SoundManager(R.raw.sound_test1)
        }
    }

    override fun initFlow() {
        AudioManager.currentMediaItem.observe(this) {
            initMediaInfo()
        }
        AudioManager.isPlayingLiveData.observe(this) {
            initPlayingStatus()
        }
        AudioManager.currentDuration.observe(this) {
            bodyBinding.seekBar.max = it.toInt()
        }
        AudioManager.currentPosition.observe(this) {
            updatePosition(it)
        }

    }

    override fun onResume() {
        super.onResume()
        AudioManager.connect()
//        initMediaInfo()
//        initPlayingStatus()
    }

    private fun initMediaInfo() {
        AudioManager.currentMediaItem.value?.mediaMetadata?.let {
            bodyBinding {
                title.text = it.title
                subtitle.text = it.artist
                bodyBinding.seekBar.max = AudioManager.getDuration().toInt()
            }
        } ?: run {
            bodyBinding {
                title.text = "音乐名"
                subtitle.text = "演唱者"
                seekBar.max = 0
            }
        }
    }

    private fun initPlayingStatus() {
        if (AudioManager.isPlayingLiveData.value == true) {
            bodyBinding.playBtn.setImageResource(R.drawable.icon_pause)
        } else {
            bodyBinding.playBtn.setImageResource(R.drawable.icon_play)
        }

    }

    private fun updatePosition(position: Long) {
        bodyBinding.seekBar.progress = position.toInt()
    }

}