package cn.lovelywhite.wxautologin;

import android.app.AndroidAppHelper;
import android.app.Application;
import android.content.Context;
import android.widget.Button;
import android.widget.Toast;

import java.lang.reflect.Field;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XC_MethodReplacement;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

public class Hook implements IXposedHookLoadPackage {

    public void handleLoadPackage(final XC_LoadPackage.LoadPackageParam lpparam) {
        if (lpparam.packageName.equals("com.tencent.mm")) {
            XposedBridge.log("Wx Loaded!");
            XposedHelpers.findAndHookMethod(Application.class, "attach", Context.class, new XC_MethodHook() {
                @Override
                protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                    ClassLoader cl = ((Context) param.args[0]).getClassLoader();
                    Class<?> hookclass = null;
                    try {
                        hookclass = cl.loadClass("com.tencent.mm.plugin.webwx.ui.ExtDeviceWXLoginUI");
                    } catch (Exception e) {
                        return;
                    }
                    final Class<?> finalHookclass = hookclass;
                    XposedHelpers.findAndHookMethod(hookclass, "initView", new XC_MethodHook() {
                        @Override
                        protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                            super.beforeHookedMethod(param);
                            XposedBridge.log("initView1!");
                        }
                        @Override
                        protected void afterHookedMethod(XC_MethodHook.MethodHookParam param)
                                throws Throwable {
                            XposedBridge.log("initView2!");
                            Field field = finalHookclass.getDeclaredField("xEY");
                            field.setAccessible(true);
                            Button btn = (Button) field.get(param.thisObject);
                            btn.performClick();
                        }
                    });
                }
            });
        }
    }
}
