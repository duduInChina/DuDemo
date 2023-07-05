package com.dudu.common.base.annotation

/**
 * 功能介绍
 * Created by Dzc on 2023/5/20.
 */
annotation class Title(
    val title: String = "",
    val isBack: Boolean = true,
    val titleType: TitleType = TitleType.DEF
)

enum class TitleType {
    DEF, // 默认固定
    COLL, // 伸缩动态
}
