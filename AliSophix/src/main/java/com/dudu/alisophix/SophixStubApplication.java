package com.dudu.alisophix;

import android.content.Context;
import android.util.Log;

import com.dudu.common.util.ContextManager;
import com.taobao.sophix.PatchStatus;
import com.taobao.sophix.SophixApplication;
import com.taobao.sophix.SophixEntry;
import com.taobao.sophix.SophixManager;
import com.taobao.sophix.listener.PatchLoadStatusListener;

import androidx.annotation.Keep;

/**
 * Sophix入口类，专门用于初始化Sophix，不应包含任何业务逻辑。
 * 此类必须继承自SophixApplication，onCreate方法不需要实现。
 * 此类不应与项目中的其他类有任何互相调用的逻辑，必须完全做到隔离。
 * AndroidManifest中设置application为此类，而SophixEntry中设为原先Application类。
 * 注意原先Application里不需要再重复初始化Sophix，并且需要避免混淆原先Application类。
 * 如有其它自定义改造，请咨询官方后妥善处理。
 *
 * queryAndLoadNewPatch方法用来请求控制台发布的补丁包，会涉及设备信息读取，所以必须在用户同意隐私协议之后调用。另外用户可根据业务情况，酌情考虑是否打开此开关。
 *
 * 但不可放在attachBaseContext中，否则无网络权限，建议放在主进程用户同意隐私协议之后的任意时刻。
 */
public class SophixStubApplication extends SophixApplication {
   private final String TAG = "SophixStubApplication";

   // 此处SophixEntry应指定真正的Application，并且保证RealApplicationStub类名不被混淆。
   // 这里的Keep是android.support包中的类，目的是为了防止这个内部静态类的类名被混淆，因为sophix内部会反射获取这个类的SophixEntry。如果项目中没有依赖android.support的话，就需要在progurad里面手动指定RealApplicationStub不被混淆
   @Keep
   @SophixEntry(MyRealApplication.class)
   static class RealApplicationStub {
   }

   @Override
   protected void attachBaseContext(Context base) {
      super.attachBaseContext(base);
//         如果需要使用MultiDex，需要在此处调用。
//         MultiDex.install(this);
      initSophix();
   }

   private void initSophix() {
      String appVersion = "0.0.0";
      try {
         appVersion = this.getPackageManager()
                 .getPackageInfo(this.getPackageName(), 0)
                 .versionName;
      } catch (Exception e) {
      }
      final SophixManager instance = SophixManager.getInstance();
      instance.setContext(this)
              .setUsingEnhance() // 适配加固模式,如果app使用了加固则需要加上此方法
              .setAppVersion(appVersion)
              .setSecretMetaData("333894431", "6541c6b1deac4e1cb2808bd64d71d310", "MIIEvQIBADANBgkqhkiG9w0BAQEFAASCBKcwggSjAgEAAoIBAQCekoBm7Sktg1AxNn9v47t3sTjyrWIEKdCSNe4i6Dg9Qkb20fNKZzcER7CylMPelljVEi9aipqYt7AZEUSzkH3jSh8X7N0DSYJtT04Vvry2CWYuQbYYmcpMjsu9gPDesmcdm0I8UbYRBMAHPe1QJmwGw0DMimugCLzhXiXdYftjyVOorhSaizrIacjReubijKV2QT4LBHbE40+72TDXQBrTyepnGHY3Nk597CYbV+gEAA9Tj7zpwXyoVJynMTB+j0+s2EvgnuXc98Yss72/x/uFQRMgtyLU4WyB8Z8C4Fmz+hffRamvkFc+h6gg161Jxe0uIdKj1UhUdcWmyEHBM62XAgMBAAECggEAVPxHfOJWU2IxNG701c7Dxl5hGw75nSmb8wCcZHD40zwWNGpJbfLrdYvGk69PWphRe8CHSItIV9j6tDShEWu7THmJOJVlzkEzecorG1RiZ12aOehV49ForqdJYoMclP4gbamsUg+o+G4HTNpPuckd1HII5Ja7H8YPFOoUeRXuWYfQv0iKa8umvTjfNf7m6Hk9iZcvh843TBUnwhbkyd3DMfmyCNffA39KqSQiGGoqlwHqWdrC81QklMQm6p1s4+f86SJ0E+vMdHprm5h3m0sRvk/gOfO2KFuMIIgVte173d7tAABGxO23VyQYW9MCjjQM+A8HesoeiiKD2YPxSCk1AQKBgQDWcrE3m1bpn/euha/pwh4QEZCYWqjj7W5DliJ95yk4Q6taIxzbNoF95LMbxlJKK6gvfYoRuq1vpRSOzLXKaGk9CDXW26K5NyKJuQD5gTtBThdzZhW2J2XwCeKkSjhWM2kySfuDvIDRo30+o/6I+I14aM+pPN4uBDiFpUwcYUwB9wKBgQC9TDGPnr+RBxi2vnCMA6fxlhm9G1bx1liYLcUb8cp3C6NFlYMkjiTd1Mr9eKVjD/cS22DbJ+m/zoFrW91FmXOHVnpNrHIZWI3lYkOFctyDkjLCfwpgqnAc2ACXFoIorZaeg44HAiimixGHfNLbP7WPtUlNZNrBa+YJUB1JwgTJYQKBgBY+g19vP46wRzLVLzAiMJgeGk+TZ91srXlZQ26n4cCyVD3fUojymHAEnweMhNCowqNadE3ufTsNdppxZZH/TptEFMGausBXlb+PbyhyXUEi5o/T0QC6CXriq527DSjz4D+VKP+7N+mG+eDbhfWTQqlwxcmaRLmlOm+ye7id5xJ1AoGAP/mxQk2vho80bgZ8uQU/TCmjd0L3JnJ/rKbDWAdmmscnHgxCrycPFJ2nIPUWSB2MLiZQXKDnofhjyunYJX6QCexTF08xnJlOE3kbQs2n8xuE5jDTTAouK8Fgqa9ku+S248moRXwYSvjzKqwoAwtMDE6DgJGWUQprSzh1PYlB3uECgYEAzzpIxZlEECOjXsZPcTwvlyo/oETVHXOCS932gKp3X1almvhzcwu1QsixuLvbancQKcfPgH2Cfvhw4z9Pdl9+iCq1JOkNqiytre1DQOhYms1NuzbVNsnGoSF1C3ov0hqo2WNuoTGUbJzRkHnT0oIf6aXpYaEg8GrL7lI2IKMWaGU=")
              .setEnableDebug(true)
              .setEnableFullLog()
              .setPatchLoadStatusStub(new PatchLoadStatusListener() {
                 @Override
                 public void onLoad(final int mode, final int code, final String info, final int handlePatchVersion) {
                    if (code == PatchStatus.CODE_LOAD_SUCCESS) {
                       Log.i(TAG, "sophix load patch success!");
                    } else if (code == PatchStatus.CODE_LOAD_RELAUNCH) {
                       // 如果需要在后台重启，建议此处用SharePreference保存状态。
                       Log.i(TAG, "sophix preload patch success. restart app to make effect.");
                    }
                 }
              }).initialize();
   }
}