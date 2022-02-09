using System.IO;
using PluginSet.Core;
using PluginSet.Core.Editor;
using UnityEditor;
using UnityEngine;

namespace PluginSet.Mdid.Editor
{
    [BuildTools]
    public static class BuildMdidTools
    {
        [OnSyncEditorSetting]
        public static void OnSyncEditorSetting(BuildProcessorContext context)
        {
            var buildParams = context.BuildChannels.Get<BuildMdidParams>();
            var targetVersion = (int)buildParams.SDKVersion;
            if (targetVersion > (int) OAIDSDKVersion.OAID_SDK_1_0_25)
                return;
            
            if (PlayerSettings.Android.minSdkVersion.CompareTo(AndroidSdkVersions.AndroidApiLevel21) < 0)
                PlayerSettings.Android.minSdkVersion = AndroidSdkVersions.AndroidApiLevel21;
        }
        
        [AndroidProjectModify]
        public static void OnAndroidProjectModify(BuildProcessorContext context, AndroidProjectManager projectManager)
        {
            projectManager.AddApplicationPlugin("mdid", "com.pluginset.mdid.MdidLifeCircle");

            var buildParams = context.BuildChannels.Get<BuildMdidParams>();
            
            var assetsPath = Path.Combine(projectManager.LibraryPath, "src", "main", "assets");
            var supplierConfigFile = Path.Combine(Application.dataPath, "..", buildParams.SupplierConfigFile);
            if (File.Exists(supplierConfigFile))
                supplierConfigFile = Path.Combine(Global.GetPackageFullPath("com.pluginset.mdid"), "supplierconfig.json");
            
            Global.CopyFileTo(supplierConfigFile, assetsPath);

            var javaScriptDir = Path.Combine(Global.GetPackageFullPath("com.pluginset.mdid"), "Java~");
            var targetHelperPath = Path.Combine(projectManager.LibraryPath, "src", "main", "java", "com", "pluginset", "mdid",
                "MdidHelper.java");
            switch (buildParams.SDKVersion)
            {
                case OAIDSDKVersion.OAID_SDK_1_0_25:
                    Global.CopyFile(Path.Combine(javaScriptDir, "MdidHelper10025.java"), targetHelperPath, null);
                    break;
                case OAIDSDKVersion.OAID_SDK_1_0_29:
                    Global.CopyFile(Path.Combine(javaScriptDir, "MdidHelper10029.java"), targetHelperPath, null);
                    break;
                default:
                    break;
            }
            
            var targetVersion = (int)buildParams.SDKVersion;
            
            if (targetVersion <= (int) OAIDSDKVersion.OAID_SDK_1_0_25)
                return;
            
            var pemCertFile = Path.Combine(Application.dataPath, "..", buildParams.PemCertFile);
            if (!File.Exists(pemCertFile))
                throw new BuildException("Cannot find mdid pem cert file: " + pemCertFile);
            
            Global.CopyFileTo(pemCertFile, assetsPath);

            var certFileName = Path.GetFileName(pemCertFile);
            projectManager.LibraryManifest.SetMetaData("mdid.cert.file", certFileName);
        }
    }
}