package com.dudu.router

import com.therouter.inject.ServiceProvider

/**
 * 功能介绍
 * Created by Dzc on 2023/9/17.
 */
@ServiceProvider(returnType = IUserService::class)
class UserService: IUserService {
    override fun getUserName(): String {
        return "DuduInChina"
    }

}