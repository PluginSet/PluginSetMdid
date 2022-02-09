package com.pluginset.mdid;

import android.app.Application;
import android.content.Context;

import com.pluginset.core.IPluginSetApplicationLifecycle;

public class MdidLifeCircle implements IPluginSetApplicationLifecycle {
    @Override
    public void onApplicationCreate(Application application) {
        MdidHelper helper = new MdidHelper();
        helper.init(application.getApplicationContext());
    }

    @Override
    public void attachBaseContext(Application application, Context base) {

    }
}
