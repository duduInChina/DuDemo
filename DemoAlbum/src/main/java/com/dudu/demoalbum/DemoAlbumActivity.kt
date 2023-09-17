package com.dudu.demoalbum

import android.Manifest
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import com.dudu.album.AlbumActivity
import com.dudu.album.AlbumConstant
import com.dudu.album.util.AlbumUtil
import com.dudu.camera.util.CameraUtil
import com.dudu.common.base.activity.BaseActivity
import com.dudu.common.base.annotation.Title
import com.dudu.common.base.annotation.TitleType
import com.dudu.common.base.view.ImageLoader
import com.dudu.common.ext.registerResult
import com.dudu.common.router.RouterPath
import com.dudu.demoalbum.databinding.ActivityDemoAlbumBinding
import com.dudu.imageloader.CoilImageLoader
import com.permissionx.guolindev.PermissionX
import com.therouter.router.Route

/**
 * 参考
 * https://mp.weixin.qq.com/s/y1CNmTB-z25ws0mKALuyNg
 * https://github.com/kelinZhou/PhotoSelector/tree/master
 * Created by Dzc on 2023/7/2.
 */
@Title(title = "相册", titleType = TitleType.COLL)
@Route(path = RouterPath.ALBUM)
class DemoAlbumActivity : BaseActivity<ActivityDemoAlbumBinding>() {

    private val imageLoader: ImageLoader = CoilImageLoader

    private val albumResult = registerResult { _, data ->
        data?.let {
            it.data?.let { uri ->
                Log.d("SysAlbum", uri.toString())
                imageLoader.load(bodyBinding.sysAlbumImage, uri)
            }
        }
    }

    private var cameraUri : Uri ?= null
    private val cameraResult = registerResult { _, _ ->
        cameraUri?.let {
            Log.d("SysCamera", it.toString())
            // 图片加载库，已自动把照片旋转调为正向
            imageLoader.load(bodyBinding.sysCameraImage, it)
        }
    }

    private lateinit var cropUri : Uri
    private val cropResult = registerResult { _, _ ->
        imageLoader.load(bodyBinding.sysCropImage, cropUri)
    }
    private val cropSelectResult = registerResult { _, data ->
        data?.let {
            it.data?.let { uri ->
                cropUri = CameraUtil.getCropUri("Crop_${System.currentTimeMillis()}.jpg")
                cropResult.launch(CameraUtil.getSysCrop(uri, cropUri))
            }
        }
    }

    private val myAlbumResult = registerResult { _, data ->
        data?.let {
            imageLoader.load(bodyBinding.myAlbumImage, it.data)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        bodyBinding {
            sysAlbumCardView.setOnClickListener {
                albumResult.launch(AlbumUtil.getSysAlbumIntent())
            }

            sysCameraCardView.setOnClickListener {
                // 没申请相机权限却可以完成拍照流程（配置文件中也没有写Camera权限）, 反而在配置中写了Camera权限则需申请，TODO 还需核实情况
                cameraUri = CameraUtil.getCameraUri(System.currentTimeMillis().toString() + ".jpg")
                cameraResult.launch(CameraUtil.getSysCameraIntent(cameraUri!!))
            }

            sysCropCardView.setOnClickListener {
                cropSelectResult.launch(AlbumUtil.getSysAlbumIntent())
            }

            myAlbumCardView.setOnClickListener {
                PermissionX.init(this@DemoAlbumActivity)
                    .permissions(if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU)
                        Manifest.permission.READ_MEDIA_IMAGES
                    else
                        Manifest.permission.READ_EXTERNAL_STORAGE)
                    .request { allGranted, _, _ ->
                        if (allGranted) {
                            AlbumConstant.imageLoader = CoilImageLoader
                            myAlbumResult.launch(Intent(this@DemoAlbumActivity, AlbumActivity::class.java))
                        }
                    }


            }

        }
    }

}