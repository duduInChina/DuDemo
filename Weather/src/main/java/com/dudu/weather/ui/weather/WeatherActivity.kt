package com.dudu.weather.ui.weather

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.dudu.common.base.activity.BaseVMActivity
import com.dudu.common.ext.utcdate
import com.dudu.common.util.StatusBarUtil
import com.dudu.weather.bean.PlaceResponse
import com.dudu.weather.bean.Weather
import com.dudu.weather.bean.getSky
import com.dudu.weather.databinding.ActivityWeatherBinding
import com.dudu.weather.databinding.ForecastItemBinding

/**
 * 功能介绍
 * Created by Dzc on 2023/5/16.
 */
class WeatherActivity : BaseVMActivity<ActivityWeatherBinding, WeatherViewModel>() {

    companion object {
        fun actionStart(context: Context, place: PlaceResponse.Place) {
            val intent = Intent(context, WeatherActivity::class.java).apply {
                putExtra("location_lng", place.location.lng)
                putExtra("location_lat", place.location.lat)
                putExtra("place_name", place.name)
            }
            context.startActivity(intent)
        }
    }

    override fun initParam() {

        StatusBarUtil.immersiveStatusBar(
            window,
            ContextCompat.getColor(this, com.dudu.common.R.color.blue)
        )

        if (viewModel.locationLng.isEmpty()) {
            viewModel.locationLng = intent.getStringExtra("location_lng") ?: ""
        }
        if (viewModel.locationLat.isEmpty()) {
            viewModel.locationLat = intent.getStringExtra("location_lat") ?: ""
        }
        if (viewModel.placeName.isEmpty()) {
            viewModel.placeName = intent.getStringExtra("place_name") ?: ""
        }
    }

    override fun initView() {

        bodyBinding {
            swipeRefresh.setColorSchemeResources(com.dudu.common.R.color.blue)
            swipeRefresh.setOnRefreshListener {
                refreshWeather()
            }
            inclueNow.navBtn.setOnClickListener {
                drawerLayout.openDrawer(GravityCompat.START)
            }
            drawerLayout.addDrawerListener(object : DrawerLayout.DrawerListener {
                override fun onDrawerSlide(drawerView: View, slideOffset: Float) {
                }

                override fun onDrawerOpened(drawerView: View) {
                }

                override fun onDrawerClosed(drawerView: View) {
                    val manager =
                        getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                    manager.hideSoftInputFromWindow(
                        drawerView.windowToken,
                        InputMethodManager.HIDE_NOT_ALWAYS
                    )
                }

                override fun onDrawerStateChanged(newState: Int) {
                }
            })
        }

    }

    override fun initFlow() {
        viewModel.weatherLiveData.observe(this) { result ->
            showWeatherInfo(result)

        }
        viewModel.weatherLoading.observe(this) { result ->
            bodyBinding.swipeRefresh.isRefreshing = result
        }

        refreshWeather()
    }

    fun refreshWeather() {
        viewModel.refreshWeather(viewModel.locationLng, viewModel.locationLat)

    }

    private fun showWeatherInfo(weather: Weather) {
        bodyBinding.inclueNow.run {
            placeName.text = viewModel.placeName
            val realtime = weather.realtime
            val currentTempText = "${realtime.temperature.toInt()} ℃"
            currentTemp.text = currentTempText
            currentSky.text = getSky(realtime.skycon).info
            val currentPM25Text = "空气指数 ${realtime.airQuality.aqi.chn.toInt()}"
            currentAQI.text = currentPM25Text
            nowLayout.setBackgroundResource(getSky(realtime.skycon).bg)
        }

        bodyBinding.inclueForecast.run {
            forecastLayout.removeAllViews()
            val daily = weather.daily
            val days = weather.daily.skycon.size
            for (i in 0 until days) {
                val skycon = daily.skycon[i]
                val temperature = daily.temperature[i]
                val viewBinding = ForecastItemBinding.inflate(
                    LayoutInflater.from(this@WeatherActivity),
                    forecastLayout,
                    false
                )
                viewBinding.dateInfo.text = skycon.date.utcdate("yyyy-MM-dd")
                val sky = getSky(skycon.value)
                viewBinding.skyIcon.setImageResource(sky.icon)
                viewBinding.skyInfo.text = sky.info
                val tempText = "${temperature.min.toInt()} ~ ${temperature.max.toInt()} ℃"
                viewBinding.temperatureInfo.text = tempText
                forecastLayout.addView(viewBinding.root)
            }
        }

        bodyBinding.inclueLifeIndex.run {
            val lifeIndex = weather.daily.lifeIndex
            coldRiskText.text = lifeIndex.coldRisk[0].desc
            dressingText.text = lifeIndex.dressing[0].desc
            ultravioletText.text = lifeIndex.ultraviolet[0].desc
            carWashingText.text = lifeIndex.carWashing[0].desc
        }
        bodyBinding.weatherLayout.visibility = View.VISIBLE

    }

    override fun onFailedViewReload() {
        refreshWeather()
    }

}