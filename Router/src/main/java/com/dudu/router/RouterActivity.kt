package com.dudu.router

import android.app.Activity
import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import com.dudu.common.base.activity.BaseActivity
import com.dudu.common.base.annotation.Title
import com.dudu.common.base.annotation.TitleType
import com.dudu.common.ext.logD
import com.dudu.common.router.RouterPath
import com.dudu.common.util.ContextManager
import com.dudu.router.databinding.ActivityRouterBinding
import com.therouter.TheRouter
import com.therouter.router.Navigator
import com.therouter.router.Route
import com.therouter.router.action.interceptor.ActionInterceptor
import com.therouter.router.interceptor.NavigationCallback

/**
 * 功能介绍
 * Created by Dzc on 2023/9/15.
 */
@Title(title = "路由", true, TitleType.COLL)
@Route(path = RouterPath.ROUTER)
class RouterActivity: BaseActivity<ActivityRouterBinding>() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        bodyBinding {
            albumLayout.setOnClickListener {
                TheRouter.build(RouterPath.ALBUM).navigation(this@RouterActivity, object : NavigationCallback(){
                    override fun onFound(navigator: Navigator) {
                        super.onFound(navigator)
                        "找到页面，即将打开".logD()
                    }

                    override fun onLost(navigator: Navigator) {
                        super.onLost(navigator)
                        "丢失页面".logD()
                    }

                    override fun onArrival(navigator: Navigator) {
                        super.onArrival(navigator)
                        "已打开".logD()
                    }

                    override fun onActivityCreated(navigator: Navigator, activity: Activity) {
                        super.onActivityCreated(navigator, activity)
                        "落地页已创建".logD()
                    }
                })
            }
            fragmentLayout.setOnClickListener {
                TheRouter.build(RouterPath.ROUTER_FRAGMENT).createFragment<Fragment>()?.let {
                    TheRouter.build(RouterPath.ROUTER_ACTIVITY_FRAGMENT)
                        .withObject("fragment", it)
                        .navigation()
                }
            }
            aopLayout.setOnClickListener {
                TheRouter.get(IUserService::class.java)?.getUserName()?.let {
                    ContextManager.showToast(it)
                }
            }
        }


        // Action
        TheRouter.addActionInterceptor(RouterPath.ROUTER_ACTION, object : ActionInterceptor(){
            override fun handle(context: Context, navigator: Navigator): Boolean {
                super.handle(context, navigator)
                ContextManager.showToast("RouterActivity收到消息")
                return false
            }
        })

    }

}