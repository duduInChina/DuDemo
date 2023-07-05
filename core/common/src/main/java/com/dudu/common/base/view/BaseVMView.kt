package com.dudu.common.base.view

/**
 * 功能介绍
 * Created by Dzc on 2023/5/14.
 */
interface BaseVMView {

    fun initParam()// 传入的参数处理

    fun initView()// 初始化View

    fun initFlow()// 监听lifedata

}