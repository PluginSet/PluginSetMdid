#if UNITY_EDITOR || UNITY_ANDROID

using System;
using PluginSet.Core;
using UnityEngine;

namespace PluginSet.Mdid
{
    public static class MdidHelper
    {
#if UNITY_EDITOR
        private static IAndroidCallable _mdidHelperApi = null;
        private static IAndroidCallable MdidHelperApi
#else
        private static AndroidJavaClass _mdidHelperApi;
        private static AndroidJavaClass MdidHelperApi
#endif
    
        {
            get
            {
#if !UNITY_EDITOR
                if (_mdidHelperApi == null)
                    _mdidHelperApi = new AndroidJavaClass("com.pluginset.mdid.MdidHelper");
#endif
                
                return _mdidHelperApi;
            }
        }

        private static object Context
        {
            get
            {
#if UNITY_ANDROID
                return AndroidHelper.CurrentActivity;
#else
                return null;
#endif
            }
        }

        public static void InitSdk()
        {
            MdidHelperApi?.CallStatic("Init", Context);
        }

        public static string GetOaid(int validSdkVersion = 0)
        {
            return MdidHelperApi?.CallStatic<string>("getOaid", validSdkVersion);
        }

        public static void GetOaidAsync(Action<string> callback)
        {
            var getter = MdidAsyncGetter.Instance.AddAsyncCallback(callback);
            MdidHelperApi?.CallStatic("setSupportOaidCallback", getter);
        }
    }
}
#endif
