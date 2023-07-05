package com.dudu.weather.ui.place

import android.content.Intent
import android.util.Log
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import androidx.recyclerview.widget.LinearLayoutManager
import com.dudu.common.base.fragment.BaseVMFragment
import com.dudu.common.bean.FailedViewModel
import com.dudu.weather.WeatherMainActivity
import com.dudu.weather.bean.PlaceResponse
import com.dudu.weather.databinding.FragmentPlaceBinding
import com.dudu.weather.ui.weather.WeatherActivity

/**
 * 选择天气地区
 * Created by Dzc on 2023/5/8.
 */
class PlaceFragment : BaseVMFragment<FragmentPlaceBinding, PlaceViewModel>() {

    private lateinit var adapter: PlaceAdapter

    override fun initParam() {
        if (activity is WeatherMainActivity) {
            viewModel.getSavedPlace().observe(this) { place ->
                Log.d("getSavePlace", place.toString())
                place?.let {
                    activity?.run {
                        WeatherActivity.actionStart(this, place)
                        finish()
                    }
                }
            }
        } else {
            showStatusBarSub(com.dudu.common.R.color.blue)
        }


    }

    override fun initView() {

        bodyBinding {
            val layoutManager = LinearLayoutManager(activity)
            recyclerView.layoutManager = layoutManager
            adapter = PlaceAdapter()
            recyclerView.adapter = adapter
            adapter.set(viewModel.placeLiveData.value)
            searchPlaceEdit.addTextChangedListener { editable ->
                val content = editable.toString()
                if (content.isNotEmpty()) {
                    viewModel.searchPlaces(content)
                } else {
                    recyclerView.visibility = View.GONE
                    bgImageView.visibility = View.VISIBLE
                    viewModel.placeLiveData.value?.clear()
                    adapter.notifyDataSetChanged()
                }
            }
        }

        adapter.setOnItemClickListener() { place ->

            val activity = this@PlaceFragment.activity
            if (activity is WeatherActivity) {

                activity.bodyBinding {
                    drawerLayout.closeDrawers()
                }
                activity.viewModel.locationLng = place.location.lng
                activity.viewModel.locationLat = place.location.lat
                activity.viewModel.placeName = place.name
                activity.refreshWeather()

            } else {
                viewModel.savePlace(place).observe(this) {
                    val intent = Intent(context, WeatherActivity::class.java).apply {
                        putExtra("location_lng", place.location.lng)
                        putExtra("location_lat", place.location.lat)
                        putExtra("place_name", place.name)
                    }
                    startActivity(intent)
                    activity?.finish()
                }
            }

        }
    }

    override fun initFlow() {

        viewModel.placeLoading.observe(this) {
            if (it) {
                bodyBinding.searchPlaceLoading.visibility = View.VISIBLE
            } else {
                bodyBinding.searchPlaceLoading.visibility = View.GONE
            }
        }

        viewModel.placeLiveData.observe(this) { result ->

            Log.d("main", result.toString())

            bodyBinding {
                recyclerView.visibility = View.VISIBLE
                bgImageView.visibility = View.GONE
                adapter.set(result)
                adapter.notifyDataSetChanged()
            }

        }

    }

    override fun onFailedViewTarget() = bodyBinding.contentFrameLayout

    override fun onFailedViewReload() {

        bodyBinding.run {
            val content = searchPlaceEdit.text.toString()
            if (content.isNotEmpty()) {
                viewModel.searchPlaces(content, 2000)
            } else {
                recyclerView.visibility = View.GONE
                bgImageView.visibility = View.VISIBLE
                viewModel.placeLiveData.value?.clear()
                adapter.notifyDataSetChanged()

                if (viewModel.failedViewLiveData.value !is FailedViewModel.HiddenView) {
                    viewModel.failedViewLiveData.value = FailedViewModel.HiddenView
                }
            }
        }

    }

}