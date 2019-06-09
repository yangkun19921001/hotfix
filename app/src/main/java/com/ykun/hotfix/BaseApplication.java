package com.ykun.hotfix;

import android.app.Application;
import android.content.Context;
import android.support.multidex.MultiDex;

import com.ykun.hotfix_module.utils.FixDexUtils;

public class BaseApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
        //初始化热修复框架
        FixDexUtils.loadFixedDex(this);
    }
}
