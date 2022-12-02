using PluginSet.Core;
using PluginSet.Core.Editor;
using UnityEngine;

namespace PluginSet.Mdid.Editor
{
    public enum OAIDSDKVersion
    {
        OAID_SDK_1_0_25 = 10025,
        OAID_SDK_1_0_29 = 10029,
        OAID_SDK_2_0_0 = 20000,
    }
    
    [BuildChannelsParams("Mdid", "信通院SDK依赖文件")]
    [VisibleCaseBoolValue("SupportAndroid", true)]
    public class BuildMdidParams: ScriptableObject
    {
        [Tooltip("选择使用的OAID版本")]
        public OAIDSDKVersion SDKVersion = OAIDSDKVersion.OAID_SDK_1_0_25;
        
        [Tooltip("MDID配置文件")]
        [BrowserFile("Choose THE supplier config file", "json")]
        public string SupplierConfigFile;
        
        [Tooltip("MDID授权文件")]
        [BrowserFile("Choose THE pem cert file", "pem")]
        [VisibleCaseEnumValue("SDKVersion", OAIDSDKVersion.OAID_SDK_1_0_25, true)]
        public string PemCertFile;
    }
}