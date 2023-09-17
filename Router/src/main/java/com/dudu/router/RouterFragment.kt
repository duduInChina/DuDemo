package com.dudu.router

import android.os.Bundle
import android.view.View
import com.dudu.common.base.fragment.BaseFragment
import com.dudu.common.router.RouterPath
import com.dudu.router.databinding.FragmentRouterBinding
import com.therouter.TheRouter
import com.therouter.router.Route

/**
 * 功能介绍
 * Created by Dzc on 2023/9/17.
 */
@Route(path = RouterPath.ROUTER_FRAGMENT, description = "Fragment测试页")
class RouterFragment :BaseFragment<FragmentRouterBinding>(){

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        bodyBinding {
            taskLayout.setOnClickListener {
                TheRouter.build(RouterPath.ROUTER_ACTION).action()
            }
        }

    }

}