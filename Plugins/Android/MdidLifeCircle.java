package com.pluginset.mdid;

import android.app.Application;
import android.content.Context;

import com.pluginset.core.IPluginSetApplicationLifecycle;

public class MdidLifeCircle implements IPluginSetApplicationLifecycle {
    @Override
    public void onApplicationCreate(Application application) {
        MdidHelper.PreInit();
    }

    @Override
    public void attachBaseContext(Application application, Context base) {

    }
}
