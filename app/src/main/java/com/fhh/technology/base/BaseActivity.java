package com.fhh.technology.base;

import android.app.Activity;
import android.app.Application;
import android.content.ComponentCallbacks;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;

import com.fhh.technology.event.DefaultEvent;
import com.fhh.technology.event.DrawerLayoutEvent;
import com.fhh.technology.utils.ToolBarOptions;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import butterknife.ButterKnife;
import butterknife.Unbinder;


/**
 * desc:
 * Created by fhh on 2018/9/13
 */

public abstract class BaseActivity extends AppCompatActivity {

    private Unbinder mUnbinder;
    public Activity mActivity;

    private static float sNoncompatDensity;
    private static float sNoncompatScaledDensity;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setCustomDensity(this, getApplication());
        setContentView(setContentLayout());
        mUnbinder = ButterKnife.bind(this);
        this.mActivity = this;
        initToolBar();
        initPresenter();
        initData();
        Log.e("TECHNOLOGY", this.getClass().getSimpleName() + "------onCreate");
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }
    }


    //参照今日头条方案解决适配问题
    public static void setCustomDensity(@NonNull Activity activity, @NonNull final Application application) {
        DisplayMetrics appDisplayMetrics = application.getResources().getDisplayMetrics();
        if (sNoncompatDensity == 0) {
            sNoncompatDensity = appDisplayMetrics.density;
            sNoncompatScaledDensity = appDisplayMetrics.scaledDensity;
            // 防止系统切换后不起作用
            application.registerComponentCallbacks(new ComponentCallbacks() {
                @Override
                public void onConfigurationChanged(Configuration newConfig) {
                    if (newConfig != null && newConfig.fontScale > 0) {
                        sNoncompatScaledDensity = application.getResources().getDisplayMetrics().scaledDensity;
                    }
                }

                @Override
                public void onLowMemory() {

                }
            });
        }
        float targetDensity = appDisplayMetrics.widthPixels / 360;
        // 防止字体变小
        float targetScaleDensity = targetDensity * (sNoncompatScaledDensity / sNoncompatDensity);
        int targetDensityDpi = (int) (160 * targetDensity);

        appDisplayMetrics.density = targetDensity;
        appDisplayMetrics.scaledDensity = targetScaleDensity;
        appDisplayMetrics.densityDpi = targetDensityDpi;

        final DisplayMetrics activityDisplayMetrics = activity.getResources().getDisplayMetrics();
        activityDisplayMetrics.density = targetDensity;
        activityDisplayMetrics.scaledDensity = targetScaleDensity;
        activityDisplayMetrics.densityDpi = targetDensityDpi;

    }

    @Override
    protected void onStart() {
        super.onStart();
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }
        Log.e("TECHNOLOGY", this.getClass().getSimpleName() + "------onStart");
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.e("TECHNOLOGY", this.getClass().getSimpleName() + "------onRestart");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.e("TECHNOLOGY", this.getClass().getSimpleName() + "------onResume");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.e("TECHNOLOGY", this.getClass().getSimpleName() + "------onPause");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.e("TECHNOLOGY", this.getClass().getSimpleName() + "------onStop");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        onDestroyActivity();
        mUnbinder.unbind();
        EventBus.getDefault().unregister(this);
        Log.e("TECHNOLOGY", this.getClass().getSimpleName() + "------onDestroy");
    }


    /**
     * 默认绑定一个事件，防止源码里面去找方法的时候找不到报错。
     */
    @Subscribe
    public void onEvent(DefaultEvent event) {
    }


    public void setToolBar(int toolBarId, ToolBarOptions options) {
        Toolbar toolbar = findViewById(toolBarId);
        if (options.titleId != 0) {
            toolbar.setTitle(options.titleId);
        } else {
            toolbar.setTitle("");
        }
        if (!TextUtils.isEmpty(options.titleString)) {
            toolbar.setTitle(options.titleString);
        }
        if (options.backgroundColor != 0) {
            toolbar.setBackgroundResource(options.backgroundColor);
        }
        if (options.logoId != 0) {
            toolbar.setLogo(options.logoId);
        }
        setSupportActionBar(toolbar);

        if (options.isNeedNavigate) {
            toolbar.setNavigationIcon(options.navigateId);
            toolbar.setContentInsetStartWithNavigation(0);
            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onNavigateUpClicked();
                }
            });
        }
    }

    private void onNavigateUpClicked() {
        onBackPressed();
    }

    public abstract int setContentLayout();

    public abstract void initToolBar();

    public abstract void initPresenter();

    public abstract void initData();

    public abstract void onDestroyActivity();
}
