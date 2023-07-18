#if UNITY_EDITOR || UNITY_ANDROID
using PluginSet.Core;

namespace PluginSet.Mdid
{
#if !UNITY_EDITOR
    [PluginRegister]
#endif
    public class MdidInitPlugin: PluginBase, IPrivacyAuthorizationCallback
    {
        public override string Name => "MdidInit";
        public void OnPrivacyAuthorization()
        {
            MdidHelper.InitSdk();
        }
    }
}
#endif