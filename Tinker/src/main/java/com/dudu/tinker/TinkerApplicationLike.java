package com.dudu.tinker;

import android.annotation.TargetApi;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import com.tencent.tinker.anno.DefaultLifeCycle;
import com.tencent.tinker.entry.DefaultApplicationLike;
import com.tencent.tinker.lib.tinker.Tinker;
import com.tencent.tinker.lib.tinker.TinkerInstaller;
import com.tencent.tinker.loader.shareutil.ShareConstants;

import androidx.multidex.MultiDex;

/**
 * Application生成类，当前只能使用java方式生成
 * Created by Dzc on 2023/9/23.
 * application:
 *  生成的application文件
 * flags:
 *  TINKER_ENABLE_ALL:支持索引、库和资源
 *  TINKER_DEX_MASK:只支持dex
 *  TINKER_NATIVE_LIBRARY_MASK:只支持lib
 *  TINKER_RESOURCE_MASK:只支持资源
 * loadVerifyFlag：
 *  是否加载时验证md5，默认false
 */
@DefaultLifeCycle(
    application = "com.dudu.tinker.MyTinkerApplication",
    flags = ShareConstants.TINKER_ENABLE_ALL,
    loadVerifyFlag = false
)
public class TinkerApplicationLike extends DefaultApplicationLike {
   public TinkerApplicationLike(Application application, int tinkerFlags, boolean tinkerLoadVerifyFlag, long applicationStartElapsedTime, long applicationStartMillisTime, Intent tinkerResultIntent) {
      super(application, tinkerFlags, tinkerLoadVerifyFlag, applicationStartElapsedTime, applicationStartMillisTime, tinkerResultIntent);
   }

   @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
   @Override
   public void onBaseContextAttached(Context base) {
      super.onBaseContextAttached(base);

      // 优先装载MultiDex，再Tinker
      MultiDex.install(base);

      installTinker();

      Tinker tinker = Tinker.with(getApplication());

   }

   @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
   public void registerActivityLifecycleCallbacks(Application.ActivityLifecycleCallbacks callback) {
      getApplication().registerActivityLifecycleCallbacks(callback);
   }

   private static boolean isInstalled = false;
   private void installTinker(){
      if(isInstalled){
         return;
      }

      TinkerInstaller.install(this);

      isInstalled = true;
   }

}
