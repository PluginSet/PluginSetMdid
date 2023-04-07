using System;
using System.Collections.Generic;
using PluginSet.Core.Editor;
using UnityEditor;

namespace PluginSet.Mdid.Editor
{
    [InitializeOnLoad]
    public static class PluginMdidFilter
    {
        static PluginMdidFilter()
        {
            PluginFilter.RegisterFilter("com.pluginset.mdid/Plugins/Android", new Dictionary<string, Func<string, BuildProcessorContext, bool>>
            {
                {"MdidLifeCircle.java", IsTargetVersionEquals(OAIDSDKVersion.None)},
                {"oaid_sdk_1.0.25.aar", IsTargetVersionNotEquals(OAIDSDKVersion.OAID_SDK_1_0_25)},
                {"oaid_sdk_1.0.29.aar", IsTargetVersionNotEquals(OAIDSDKVersion.OAID_SDK_1_0_29)},
                {"oaid_sdk_2.0.0.aar", IsTargetVersionNotEquals(OAIDSDKVersion.OAID_SDK_2_0_0)},
            });
        }

        private static Func<string, BuildProcessorContext, bool> IsTargetVersionNotEquals(OAIDSDKVersion targetVersion)
        {
            return delegate(string _, BuildProcessorContext context)
            {
                if (!context.BuildTarget.Equals(BuildTarget.Android))
                    return true;
                
                var bulidParams = context.BuildChannels.Get<BuildMdidParams>();
                return bulidParams.SDKVersion != targetVersion;
            };
        }
        
        private static Func<string, BuildProcessorContext, bool> IsTargetVersionEquals(OAIDSDKVersion targetVersion)
        {
            return delegate(string _, BuildProcessorContext context)
            {
                if (!context.BuildTarget.Equals(BuildTarget.Android))
                    return true;
                
                var bulidParams = context.BuildChannels.Get<BuildMdidParams>();
                return bulidParams.SDKVersion == targetVersion;
            };
        }
    }
}