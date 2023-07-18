package com.pluginset.mdid;

import android.content.Context;
import android.os.Build;
import android.util.Log;

import com.bun.miitmdid.core.ErrorCode;
import com.bun.miitmdid.core.MdidSdkHelper;
import com.bun.miitmdid.interfaces.IIdentifierListener;
import com.bun.miitmdid.interfaces.IdSupplier;
import com.pluginset.core.IPluginSetCallback;

public class MdidHelper implements IIdentifierListener {

    public static final String TAG = "MdidHelper";

    private static IdSupplier mIdSupplier = null;

    private static IPluginSetCallback supportOaidCallback = null;

    public boolean isSDKLogOn = false;          // TODO （1）设置 是否开启sdk日志

    public MdidHelper() {
    }

    public void init(Context context) {
        int result = MdidSdkHelper.InitSdk(context, isSDKLogOn, this);
        IdSupplier unSupportSupplier = new IdSupplier() {
            @Override
            public boolean isSupported() {
                return false;
            }

            @Override
            public String getOAID() {
                return "";
            }

            @Override
            public String getVAID() {
                return "";
            }

            @Override
            public String getAAID() {
                return "";
            }
        };

        switch (result) {
            case ErrorCode.INIT_ERROR_MANUFACTURER_NOSUPPORT: {
                Log.d(TAG, "MdidSdkHelper.InitSdk fail::: 不支持的厂商");
                OnSupport(false, unSupportSupplier);
            } break;
            case ErrorCode.INIT_ERROR_DEVICE_NOSUPPORT: {
                Log.d(TAG, "MdidSdkHelper.InitSdk fail::: 不支持的设备");
                OnSupport(false, unSupportSupplier);
            } break;
            case ErrorCode.INIT_ERROR_LOAD_CONFIGFILE: {
                Log.d(TAG, "MdidSdkHelper.InitSdk fail::: 加载配置文件失败");
                OnSupport(false, unSupportSupplier);
            } break;
            case ErrorCode.INIT_ERROR_RESULT_DELAY: {
                Log.d(TAG, "MdidSdkHelper.InitSdk fail::: 信息将会延迟返回，获取数据可能在异步线程，取决于设备");
            } break;
            case ErrorCode.INIT_HELPER_CALL_ERROR: {
                Log.d(TAG, "MdidSdkHelper.InitSdk fail::: 反射调用失败");
                OnSupport(false, unSupportSupplier);
            } break;
            default:
                Log.d(TAG, "MdidSdkHelper.InitSdk result ============= " + result);
                break;
        }
    }
    
    private static MdidHelper _helper;
    public static void PreInit() {
        if (_helper == null)
            _helper = new MdidHelper();
    }
    
    public static void Init(Context context) {
        PreInit();
        _helper.init(context);
    }
    
    public static String getOaid(int validSdkVersion) {
        if (Build.VERSION.SDK_INT < validSdkVersion)
            return "";

        return mIdSupplier != null ? mIdSupplier.getOAID() : "";
    }

    public static void setSupportOaidCallback(IPluginSetCallback callback)
    {
        supportOaidCallback = callback;
        if (mIdSupplier != null && callback != null)
            callback.onSuccess(mIdSupplier.getOAID());
    }

    @Override
    public void OnSupport(boolean isSupport, IdSupplier idSupplier) {
        if(idSupplier==null) {
            return;
        }

        mIdSupplier = idSupplier;

        String oaid=idSupplier.getOAID();
        String vaid=idSupplier.getVAID();
        String aaid=idSupplier.getAAID();
        StringBuilder builder=new StringBuilder();
        builder.append("support: ").append(isSupport?"true":"false").append("\n");
        builder.append("OAID: ").append(oaid).append("\n");
        builder.append("VAID: ").append(vaid).append("\n");
        builder.append("AAID: ").append(aaid).append("\n");
        String idstext=builder.toString();
        Log.d(TAG, "onSupport get ids:::::::::: " + idstext);

        if (supportOaidCallback != null)
            supportOaidCallback.onSuccess(oaid);
    }
}
