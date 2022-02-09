#if UNITY_EDITOR || UNITY_ANDROID

using System;
using PluginSet.Core;
using UnityEngine;

namespace PluginSet.Mdid
{
    public static class MdidHelper
    {
#if UNITY_EDITOR
        private static IAndroidCallable _mdidHelperApi;
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
