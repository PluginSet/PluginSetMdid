package com.pluginset.mdid;

import android.content.Context;
import android.os.Build;
import android.util.Log;

import com.bun.miitmdid.core.InfoCode;
import com.bun.miitmdid.core.MdidSdkHelper;
import com.bun.miitmdid.interfaces.IIdentifierListener;
import com.bun.miitmdid.interfaces.IdSupplier;
import com.bun.miitmdid.pojo.IdSupplierImpl;
import com.pluginset.core.IPluginSetCallback;
import com.pluginset.devices.Utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class MdidHelper implements IIdentifierListener {

    public static final String TAG = "MdidHelper";

    private static IdSupplier mIdSupplier = null;

    private static IPluginSetCallback supportOaidCallback = null;

    private boolean isSDKLogOn = false;          // TODO （1）设置 是否开启sdk日志

    public MdidHelper() {
        System.loadLibrary("msaoaidsec");  // TODO （3）SDK初始化操作
    }

    public void init(Context context)
    {
        // 注：请在调用前设置一次后就不再更改，否则可能导致回调丢失、重复等问题
        try {
            MdidSdkHelper.setGlobalTimeout(5000);
        } catch (Error e) {
            e.printStackTrace();
        }

        try {
            String certFileName = Utils.GetApplicationMetaString(context, "mdid.cert.file", null);
            if (certFileName != null && MdidSdkHelper.InitCert(context, loadPemFromAssetFile(context, certFileName))) {
                Log.d(TAG, "cert init success::" + certFileName);
            } else {
                Log.d(TAG, "cert init failed::" + certFileName);
            }
        } catch (Error e) {
            e.printStackTrace();
        }

        // TODO （5）调用SDK获取ID
        int code = 0;
        try {
            code = MdidSdkHelper.InitSdk(context, isSDKLogOn, this);
        } catch (Error e) {
            e.printStackTrace();
        }

        // TODO （6）根据SDK返回的code进行不同处理
        IdSupplierImpl unsupportedIdSupplier = new IdSupplierImpl();
        if(code == InfoCode.INIT_ERROR_CERT_ERROR){                         // 证书未初始化或证书无效，SDK内部不会回调onSupport
            // APP自定义逻辑
            Log.w(TAG,"cert not init or check not pass");
            onSupport(unsupportedIdSupplier);
        }else if(code == InfoCode.INIT_ERROR_DEVICE_NOSUPPORT){             // 不支持的设备, SDK内部不会回调onSupport
            // APP自定义逻辑
            Log.w(TAG,"device not supported");
            onSupport(unsupportedIdSupplier);
        }else if( code == InfoCode.INIT_ERROR_LOAD_CONFIGFILE){            // 加载配置文件出错, SDK内部不会回调onSupport
            // APP自定义逻辑
            Log.w(TAG,"failed to load config file");
            onSupport(unsupportedIdSupplier);
        }else if(code == InfoCode.INIT_ERROR_MANUFACTURER_NOSUPPORT){      // 不支持的设备厂商, SDK内部不会回调onSupport
            // APP自定义逻辑
            Log.w(TAG,"manufacturer not supported");
            onSupport(unsupportedIdSupplier);
        }else if(code == InfoCode.INIT_ERROR_SDK_CALL_ERROR){             // sdk调用出错, SSDK内部不会回调onSupport
            // APP自定义逻辑
            Log.w(TAG,"sdk call error");
            onSupport(unsupportedIdSupplier);
        } else if(code == InfoCode.INIT_INFO_RESULT_DELAY) {             // 获取接口是异步的，SDK内部会回调onSupport
            Log.i(TAG, "result delay (async)");
        }else if(code == InfoCode.INIT_INFO_RESULT_OK){                  // 获取接口是同步的，SDK内部会回调onSupport
            Log.i(TAG, "result ok (sync)");
        }else {
            // sdk版本高于DemoHelper代码版本可能出现的情况，无法确定是否调用onSupport
            // 不影响成功的OAID获取
            Log.w(TAG,"getDeviceIds: unknown code: " + code);
        }
    }

    @Override
    public void onSupport(IdSupplier supplier) {
        if(supplier==null) {
            Log.w(TAG, "onSupport: supplier is null");
            return;
        }

        // 获取Id信息
        // 注：IdSupplier中的内容为本次调用MdidSdkHelper.InitSdk()的结果，不会实时更新。 如需更新，需调用MdidSdkHelper.InitSdk()
        boolean isSupported = supplier.isSupported();
        boolean isLimited  = supplier.isLimited();
        String oaid=supplier.getOAID();
        String vaid=supplier.getVAID();
        String aaid=supplier.getAAID();

        //TODO (7) 自定义后续流程，以下显示到UI的示例
        String idsText= "support: " + (isSupported ? "true" : "false") +
                "\nlimit: " + (isLimited ? "true" : "false") +
                "\nOAID: " + oaid +
                "\nVAID: " + vaid +
                "\nAAID: " + aaid + "\n";
        Log.d(TAG, "onSupport: ids: \n" + idsText);

        mIdSupplier = supplier;
        if (supportOaidCallback != null)
            supportOaidCallback.onSuccess(oaid);
    }

    private static MdidHelper _helper = null;
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

    /**
     * 从asset文件读取证书内容
     * @param context
     * @param assetFileName
     * @return 证书字符串
     */
    public static String loadPemFromAssetFile(Context context, String assetFileName){
        try {
            InputStream is = context.getAssets().open(assetFileName);
            BufferedReader in = new BufferedReader(new InputStreamReader(is));
            StringBuilder builder = new StringBuilder();
            String line;
            while ((line = in.readLine()) != null){
                builder.append(line);
                builder.append('\n');
            }
            return builder.toString();
        } catch (IOException e) {
            Log.e(TAG, "loadPemFromAssetFile failed");
            return "";
        }
    }
}
