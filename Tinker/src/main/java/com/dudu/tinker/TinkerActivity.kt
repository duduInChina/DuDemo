package com.dudu.tinker

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.dudu.tinker.databinding.ActivityTinkerBinding
import com.tencent.tinker.lib.tinker.Tinker
import com.tencent.tinker.lib.tinker.TinkerInstaller
import com.tencent.tinker.loader.shareutil.ShareTinkerInternals

/**
 * 功能介绍
 * Created by Dzc on 2023/9/22.
 */
class TinkerActivity: AppCompatActivity() {

    private val tinker: Tinker by lazy {
        Tinker.with(applicationContext)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val tinkerBinding = ActivityTinkerBinding.inflate(layoutInflater)
        setContentView(tinkerBinding.root)

        tinkerBinding.run {
            val text = "当前修复状态：已修复 ${tinker.isTinkerLoaded}"
            fixText.text = text

            loadPatch.setOnClickListener {
                val files = application.externalCacheDir?.listFiles()
                if(!files.isNullOrEmpty()){
                    TinkerInstaller.onReceiveUpgradePatch(applicationContext, files[0].absolutePath)
                }else{
                    Toast.makeText(application, "没找到补丁文件", Toast.LENGTH_LONG).show()
                }
            }

            killSelf.setOnClickListener {
                ShareTinkerInternals.killAllOtherProcess(applicationContext);
                android.os.Process.killProcess(android.os.Process.myPid());
            }

            cleanPatch.setOnClickListener {
                Tinker.with(applicationContext).cleanPatch();
            }
        }


        Log.d("TinkerDemo","FixBug")
//        Log.d("TinkerDemo","FixBug")

    }
}