package com.dudu.datastore


import android.app.Application
import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.doublePreferencesKey
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.core.stringSetPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

/**
 * datastore调用封装，代理
 * 模仿博客 https://mp.weixin.qq.com/s/3BecCDPGo1OYDE9bjDl6tA
 * datastore和MMKV对比，为何使用datastore https://juejin.cn/post/7112268981163016229
 *
 * 为了正确使用 DataStore，请始终谨记以下规则：

1、请勿在同一进程中为给定文件(相同文件名)创建多个 DataStore 实例，否则会破坏所有 DataStore 功能。如果给定文件在同一进程中有多个有效的 DataStore，DataStore 在读取或更新数据时将抛出 IllegalStateException。

2、DataStore 的通用类型必须不可变。更改 DataStore 中使用的类型会导致 DataStore 提供的所有保证失效，并且可能会造成严重的、难以发现的 bug。强烈建议您使用可保证不可变性、具有简单的 API 且能够高效进行序列化的协议缓冲区。

3、切勿在同一个文件中混用 SingleProcessDataStore 和 MultiProcessDataStore。如果您打算从多个进程访问 DataStore，请始终使用 MultiProcessDataStore。

 * Created by Dzc on 2023/5/17.
 */
open class DataStoreOwner(name: String) : IDataStoreOwner {
    private val Context.dataStore by preferencesDataStore(name)
    override val dataStore get() = context.dataStore
}

interface IDataStoreOwner {
    val context: Context get() = application

    val dataStore: DataStore<Preferences>

    fun intPreference(default: Int? = null): ReadOnlyProperty<IDataStoreOwner, DataStorePreference<Int>> =
        PreferenceProperty(::intPreferencesKey, default)

    fun doublePreference(default: Double? = null): ReadOnlyProperty<IDataStoreOwner, DataStorePreference<Double>> =
        PreferenceProperty(::doublePreferencesKey, default)

    fun longPreference(default: Long? = null): ReadOnlyProperty<IDataStoreOwner, DataStorePreference<Long>> =
        PreferenceProperty(::longPreferencesKey, default)

    fun floatPreference(default: Float? = null): ReadOnlyProperty<IDataStoreOwner, DataStorePreference<Float>> =
        PreferenceProperty(::floatPreferencesKey, default)

    fun booleanPreference(default: Boolean? = null): ReadOnlyProperty<IDataStoreOwner, DataStorePreference<Boolean>> =
        PreferenceProperty(::booleanPreferencesKey, default)

    fun stringPreference(default: String? = null): ReadOnlyProperty<IDataStoreOwner, DataStorePreference<String>> =
        PreferenceProperty(::stringPreferencesKey, default)

    fun stringSetPreference(default: Set<String>? = null): ReadOnlyProperty<IDataStoreOwner, DataStorePreference<Set<String>>> =
        PreferenceProperty(::stringSetPreferencesKey, default)

    class PreferenceProperty<V>(
        private val key: (String) -> Preferences.Key<V>,
        private val default: V? = null,
    ) : ReadOnlyProperty<IDataStoreOwner, DataStorePreference<V>> {
        private var cache: DataStorePreference<V>? = null

        override fun getValue(thisRef: IDataStoreOwner, property: KProperty<*>): DataStorePreference<V> =
            cache ?: DataStorePreference(thisRef.dataStore, key(property.name), default).also { cache = it }
    }

    companion object {
        internal lateinit var application: Application
    }
}