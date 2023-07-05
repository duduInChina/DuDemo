package com.dudu.album

import android.content.Intent
import android.util.Log
import androidx.recyclerview.widget.GridLayoutManager
import com.dudu.album.databinding.ActivityAlbumBinding
import com.dudu.common.base.activity.BaseVMActivity
import com.dudu.common.base.annotation.Title

/**
 * 功能介绍
 * Created by Dzc on 2023/7/3.
 */
@Title("图库")
class AlbumActivity : BaseVMActivity<ActivityAlbumBinding, AlbumViewModel>() {

    private lateinit var adapter: AlbumAdapter

    override fun initView() {
        bodyBinding {
            recyclerView.layoutManager = GridLayoutManager(this@AlbumActivity, 4)
            adapter = AlbumAdapter()
            recyclerView.adapter = adapter
            adapter.set(viewModel.mediaDataLiveData.value)
            adapter.setOnItemClickListener { m ->

                setResult(RESULT_OK, Intent().setData(m.getUri()))
                finish()
            }
        }

        viewModel.mediaLoad()
    }

    override fun initFlow() {
        viewModel.mediaDataLiveData.observe(this) { mediaData ->
            Log.d("initFlow", mediaData.toString())
            adapter.set(mediaData)
        }
    }
}