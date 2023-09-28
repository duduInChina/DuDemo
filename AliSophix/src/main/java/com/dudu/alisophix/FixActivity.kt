package com.dudu.alisophix

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import com.dudemo.databinding.ActivityFixBinding
import com.dudu.common.base.activity.BaseActivity
import com.dudu.common.base.annotation.Title
import com.dudu.common.base.annotation.TitleType
import com.taobao.sophix.SophixManager


/**
 * 补丁版本说明
    补丁是针对客户端具体某个版本的，补丁和具体版本绑定。
    示例：应用当前版本号是1.1.0，那么只能在后台查询到1.1.0版本对应发布的补丁，而查询不到之前1.0.0旧版本发布的补丁。
    针对某个具体版本发布的新补丁，必须包含所有的bugfix，而不能依赖补丁递增修复的方式，因为应用仅可能加载一个补丁。
    示例：针对1.0.0版本在后台发布了一个补丁版本号为1的补丁修复了bug1，然后发现此时针对这个版本补丁1修复的不完全，
    代码还有bug2，在后台重新发布一个补丁版本号为2的补丁，那么此时补丁2就必须同时包含bug1和bug2的修复；如果只包含bug2的修复，bug1的修复就会失效。

https://help.aliyun.com/document_detail/434864.html?spm=a2c4g.434862.0.0.4f7b25d6hlPC28
 * Created by Dzc on 2023/9/27.
 */
@Title("阿里热修复", isBack = false, titleType = TitleType.COLL)
class FixActivity:BaseActivity<ActivityFixBinding>() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        bodyBinding {
            val text = "当前修复状态：未修复"
            fixText.text = text

            loadPatch.setOnClickListener {
                SophixManager.getInstance().queryAndLoadNewPatch()
            }

            killSelf.setOnClickListener {
                SophixManager.getInstance().killProcessSafely()
            }

            cleanPatch.setOnClickListener {
                // 清空本地补丁，并且不再拉取被清空的版本的补丁。正常情况下不需要开发者自己调用，因为Sophix内部会判断对补丁引发崩溃的情况进行自动清空。
                SophixManager.getInstance().cleanPatches()
            }
        }

//        "fixBug".logD()
    }

}