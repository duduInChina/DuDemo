package com.dudu.weather.ui.place

import android.view.LayoutInflater
import android.view.ViewGroup
import com.dudu.common.base.adapter.CommonViewHolder
import com.dudu.common.base.adapter.RecyclerAdapter
import com.dudu.weather.bean.PlaceResponse
import com.dudu.weather.databinding.ItemPlaceBinding

/**
 * 功能介绍
 * Created by Dzc on 2023/5/14.
 */
class PlaceAdapter : RecyclerAdapter<PlaceResponse.Place, ItemPlaceBinding>() {
    override fun viewBinding(parent: ViewGroup): ItemPlaceBinding {
        return ItemPlaceBinding.inflate(LayoutInflater.from(parent.context), parent, false)
    }

    override fun bindViewHolder(
        holder: CommonViewHolder<ItemPlaceBinding>,
        m: PlaceResponse.Place,
        position: Int
    ) {

        holder.viewBanding {
            placeName.text = m.name
            placeAddress.text = m.address
        }

    }
}