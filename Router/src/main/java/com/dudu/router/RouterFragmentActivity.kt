package com.dudu.router

import android.os.Bundle
import androidx.fragment.app.Fragment
import com.dudu.common.base.activity.BaseActivity
import com.dudu.common.router.RouterPath
import com.dudu.router.databinding.ActivityRouterFragmentBinding
import com.therouter.TheRouter
import com.therouter.router.Autowired
import com.therouter.router.Route

/**
 * 功能介绍
 * Created by Dzc on 2023/9/17.
 */
@Route(path = RouterPath.ROUTER_ACTIVITY_FRAGMENT, description = "Fragment容器页")
class RouterFragmentActivity: BaseActivity<ActivityRouterFragmentBinding>() {

    // 通过参数依赖注入Fragment
    @Autowired
    lateinit var fragment: Fragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        TheRouter.inject(this) // 依赖注入写入，通常合适情况下可写在BaseActivity

        supportFragmentManager.beginTransaction()
            .replace(R.id.container, fragment)
            .commitAllowingStateLoss()

    }

}