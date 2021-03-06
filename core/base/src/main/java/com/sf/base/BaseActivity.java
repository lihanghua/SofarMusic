package com.sf.base;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.sf.base.callback.CallBackIntent;
import com.sf.base.util.AppManager;
import com.sf.base.view.LoadView;
import com.sf.libskin.base.SkinBaseActivity;
import com.sf.utility.AppUtil;

/**
 * Created by sufan on 17/2/28.
 */

public class BaseActivity extends SkinBaseActivity implements ActivityInterface {
  private static final String TAG = "BaseActivity";

  public BaseActivity baseAt;
  public View rootView;

  // 设置启动和退出Activity的动画
  public static final String START_EXIT_PAGE_ANIMATION = "start_exit_page_animation";
  public static final String START_ENTER_PAGE_ANIMATION = "start_enter_page_animation";
  public static final String FINISH_EXIT_PAGE_ANIMATION = "finish_exit_page_animation";
  public static final String FINISH_ENTER_PAGE_ANIMATION = "finish_enter_page_animation";
  public static final int NO_ANIM = 0;


  public FrameLayout mContentContainer; // 获取根视野
  private View mLoadView;
  private LoadView loadView;

  // activity请求回调相关
  private static final int REQUESTCODE = 100;
  private static final int RESULTCODE = 101;
  private CallBackIntent CallBackIntent;


  @Override
  protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    baseAt = this;
    rootView = getWindow().getDecorView().findViewById(android.R.id.content);
    AppManager.getAppManager().addActivity(baseAt);

    // 初始化loadview
    mContentContainer = (FrameLayout) rootView;
    mLoadView = LayoutInflater.from(getBaseContext()).inflate(R.layout.layout_loadview, null);
    loadView = (LoadView) mLoadView.findViewById(R.id.loadview);
    dynamicAddView(loadView, "loadColor", R.color.themeColor);
    dynamicAddView(loadView, "loadTextColor", R.color.main_text_color);
    loadView.setVisibility(View.GONE); // 默认隐藏

  }

  /**
   * 在onCreate之后执行
   *
   * @param savedInstanceState
   */
  @Override
  protected void onPostCreate(@Nullable Bundle savedInstanceState) {
    super.onPostCreate(savedInstanceState);
    // 将loadview放进布已经加载好的xml布局里
    FrameLayout.LayoutParams layoutParams =
        new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT);
    layoutParams.gravity = Gravity.CENTER;
    mContentContainer.addView(mLoadView, layoutParams);
  }


  public void show() {
    loadView.setVisibility(View.VISIBLE);
  }


  public void dismiss() {
    loadView.setVisibility(View.GONE);
  }

  /**
   * activity请求回调
   * 继承自该activity的页面，统一调用该方法
   */
  @Override
  public void startActivityForResult(Intent intent, CallBackIntent callBackIntent) {
    this.CallBackIntent = callBackIntent;
    startActivityForResult(intent, REQUESTCODE);
  }

  @Override
  public void setActivityResultCallback(Intent intent) {
    if (intent == null) {
      setResult(RESULTCODE, new Intent());
    } else {
      setResult(RESULTCODE, intent);
    }
    finish();
  }

  @Override
  protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    if (RESULTCODE == resultCode) {
      // 跳转自己写的页面
      if (CallBackIntent != null) {
        CallBackIntent.onResult(data);
      }
    } else if (REQUESTCODE == requestCode && resultCode != 0) {
      // 跳转系统页面
      if (CallBackIntent != null) {
        CallBackIntent.onResult(data);
      }
    }
    super.onActivityResult(requestCode, resultCode, data);
  }

  @Override
  protected void onStop() {
    super.onStop();
    if (!AppUtil.isAppOnForeground(this)) {

    }
  }

  @Override
  protected void onDestroy() {
    super.onDestroy();
    AppManager.getAppManager().removeActivity(baseAt);
  }

  @Override
  public void finish() {
    super.finish();
    overridePendingTransition(
        getIntent().getIntExtra(FINISH_ENTER_PAGE_ANIMATION, NO_ANIM),
        getIntent().getIntExtra(FINISH_EXIT_PAGE_ANIMATION,
            R.anim.activity_animation_out_to_right));
  }

}


