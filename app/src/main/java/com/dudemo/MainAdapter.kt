package com.dudemo

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import com.dudemo.databinding.ItemMainBinding
import com.dudu.common.base.adapter.CommonViewHolder
import com.dudu.common.base.adapter.RecyclerAdapter
import com.dudu.common.util.ContextManager

/**
 * 功能介绍
 * Created by Dzc on 2023/5/21.
 */
class MainAdapter : RecyclerAdapter<MainData, ItemMainBinding>(){
    override fun viewBinding(parent: ViewGroup): ItemMainBinding {
       return ItemMainBinding.inflate(LayoutInflater.from(parent.context), parent, false)
    }

    override fun bindViewHolder(
        holder: CommonViewHolder<ItemMainBinding>,
        m: MainData,
        position: Int
    ) {
        holder.viewBanding {
            titleTextView.text = m.title
            contentTitleView.text = m.content
            cardView.setOnClickListener {
                onItemClickListener?.let {
                    it.itemClick(recyclerView.id, m, position)
                }
            }
        }
    }

}