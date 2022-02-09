using System;
using System.Collections.Generic;
using PluginSet.Core;

namespace PluginSet.Mdid
{
    public class MdidAsyncGetter: PluginSetCallbackBase
    {
        private static MdidAsyncGetter _instance;
        
        public static MdidAsyncGetter Instance
        {
            get
            {
                if (_instance == null)
                    _instance = new MdidAsyncGetter();

                return _instance;
            }
        }
        
        public override void onSuccess(string result)
        {
            CallbackResult(result);
        }

        public override void onFailed(int code, string message)
        {
            CallbackResult(null);
        }

        private void CallbackResult(string result)
        {
            foreach (var callback in callbacks)
            {
                callback.Invoke(result);
            }
            callbacks.Clear();
        }

        private List<Action<string>> callbacks = new List<Action<string>>();
        public MdidAsyncGetter AddAsyncCallback(Action<string> callback)
        {
            callbacks.Add(callback);
            return this;
        }
    }
}