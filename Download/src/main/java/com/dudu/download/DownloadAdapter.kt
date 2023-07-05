package com.dudu.download

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.dudu.download.bean.DownloadStatus
import com.dudu.download.databinding.ItemDownloadBinding
import com.google.android.material.button.MaterialButton
import java.lang.ref.SoftReference

/**
 * 功能介绍
 * Created by Dzc on 2023/6/3.
 */
class DownloadAdapter : RecyclerView.Adapter<DownloadAdapter.ViewHolder>() {
    var data: MutableList<DownloadTask> = ArrayList()

    inner class ViewHolder(binding: ItemDownloadBinding) : RecyclerView.ViewHolder(binding.root) {
        private var itemViewBinding: ItemDownloadBinding? = binding

        private fun viewBinding(block: ItemDownloadBinding.() -> Unit) {
            itemViewBinding?.run {
                block(this)
            }
        }

        var downloadTask: DownloadTask? = null

        fun bindData(task: DownloadTask) {

            downloadTask = task

            downloadTask?.let {
                it.setViewHolderRefreshListener(object : AbsDownloadListener(SoftReference(this)) {
                    override fun refreshStatus(status: DownloadStatus) {
                        userTag.get()?.let { userTag ->
                            val vh = userTag as DownloadAdapter.ViewHolder
                            vh.refresh()
                        }
                    }

                })
            }

            viewBinding {
                nameText.text = task.taskData.fileName

                taskBtn.setOnClickListener {
                    downloadTask?.let { downloadTask ->
                        when (downloadTask.status) {
                            is DownloadStatus.Waiting, is DownloadStatus.Downloading ->
                                DownloadManager.stopDownloadTask(downloadTask)
                            is DownloadStatus.Stopped ->
                                DownloadManager.continueDownloadTask(downloadTask)
                            is DownloadStatus.Done, is DownloadStatus.Failed, is DownloadStatus.None ->
                                DownloadManager.resumeDownloadTask(downloadTask)
                        }
                    }
                }
            }

            refresh()
        }

        fun refresh() {
            viewBinding {
                downloadTask?.let { task ->
                    when (task.status) {
                        is DownloadStatus.Downloading -> {
                            resetBtn(taskBtn, task)
                            taskSize.text = DownloadConstant.fileSizeString(task.taskData)
                            taskProgressBar.progress = DownloadConstant.downloadProgress(task.taskData)
                        }

                        is DownloadStatus.Done -> {
                            resetBtn(taskBtn, task)
                            taskSize.text = DownloadConstant.fileSizeString(task.taskData)
                            taskProgressBar.progress = 100
                        }

                        else -> {
                            resetBtn(taskBtn, task)
                            taskSize.text = DownloadConstant.fileSizeString(task.taskData)
                            taskProgressBar.progress = DownloadConstant.downloadProgress(task.taskData)
                        }
                    }
                }
            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val viewBinding =
            ItemDownloadBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(viewBinding)
    }

    override fun getItemCount() = data.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val m = data[position]
        holder.bindData(m)
    }

    fun set(list: MutableList<DownloadTask>?) {
        if (list != null) {
            data = list
            notifyDataSetChanged()
        } else {
            data.clear()
            notifyDataSetChanged()
        }
    }

    private fun resetBtn(btn: MaterialButton, task: DownloadTask) {
        btn.text = DownloadConstant.downloadStatusToText(task.status)
    }

}