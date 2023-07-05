package com.dudu.weather

import android.os.Bundle
import androidx.core.content.ContextCompat
import com.dudu.common.base.activity.BaseActivity
import com.dudu.common.util.StatusBarUtil
import com.dudu.network.RetrofitManager
import com.dudu.weather.databinding.ActivityWeatherMainBinding

/**
 * 仿第一行代码weather项目
 * Created by Dzc on 2023/5/6.
 */
class WeatherMainActivity : BaseActivity<ActivityWeatherMainBinding>() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        StatusBarUtil.setStatusBarColor(window, ContextCompat.getColor(this, com.dudu.common.R.color.blue))

    }

}