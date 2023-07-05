package com.dudu.common.widget;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.dudu.common.R;

import androidx.annotation.StyleRes;
import androidx.appcompat.app.AppCompatDialog;

/**
 * 功能介绍
 * Created by Dzc on 2023/5/14.
 */

public class MiniLoadingDialog extends AppCompatDialog {
   private MiniLoadingView mLoadingView;
   private TextView mTvTipMessage;

   public MiniLoadingDialog(Context context) {
      this(context, R.style.CommonDialog_Custom_MiniLoading, R.layout.dialog_loading_mini);
      initView();
   }

   public MiniLoadingDialog(Context context, String tipMessage) {
      this(context, R.style.CommonDialog_Custom_MiniLoading, R.layout.dialog_loading_mini);
      initView();
   }

   public MiniLoadingDialog(Context context, @StyleRes int themeResId) {
      this(context, themeResId, R.layout.dialog_loading_mini);
      initView();
   }

   public MiniLoadingDialog(Context context, @StyleRes int themeResId, String tipMessage) {
      this(context, themeResId, R.layout.dialog_loading_mini);
      initView();
   }

   public MiniLoadingDialog(Context context, int theme, int layoutId) {
      super(context, theme);
      init(layoutId);

   }

   public void init(int layoutId) {
      View view = getLayoutInflater().inflate(layoutId, null);
      init(view);
   }

   private void init(View view) {
      setContentView(view);
      setCanceledOnTouchOutside(true);
   }

   private void initView() {
      mLoadingView = findViewById(R.id.miniLoadingView);
      mTvTipMessage = findViewById(R.id.tipView);

      updateMessage("");

      setCancelable(false);
      setCanceledOnTouchOutside(false);

   }



   /**
    * 更新提示信息
    *
    * @param tipMessage 提示信息
    */
   public void updateMessage(String tipMessage) {
      if (mTvTipMessage != null) {
         if (!TextUtils.isEmpty(tipMessage)) {
            mTvTipMessage.setText(tipMessage);
            mTvTipMessage.setVisibility(View.VISIBLE);
         } else {
            mTvTipMessage.setText("");
            mTvTipMessage.setVisibility(View.GONE);
         }
      }
   }

   protected void performShow() {
      super.show();
      if (mLoadingView != null) {
         mLoadingView.start();
      }
   }

   /**
    * 隐藏加载
    */
   @Override
   public void dismiss() {
      if (mLoadingView != null) {
         mLoadingView.stop();
      }
      super.dismiss();
   }
}
