package com.example.behavior;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.ContentResolver;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Environment;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.example.behavior.task.ContactTask;
import com.example.behavior.task.DCIMTask;
import com.example.behavior.task.LbsTask;
import com.example.behavior.task.SMSTask;
import com.example.behavior.utils.Util;

import java.io.File;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

@SuppressLint("PrivateApi")
public class MainHook implements IXposedHookLoadPackage {
    private Context context;
    private XC_LoadPackage.LoadPackageParam loadPackageParam;

    @Override
    public void handleLoadPackage(XC_LoadPackage.LoadPackageParam loadPackageParam) throws Throwable {
        this.loadPackageParam = loadPackageParam;
        log("load::" + loadPackageParam.packageName);
        // 跳过系统应用
        if ((loadPackageParam.appInfo.flags & ApplicationInfo.FLAG_SYSTEM) > 0) {
            return;
        }
        try {
            this.hookContext(loadPackageParam);
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("@@", e.getMessage());
        }
        try {
            // 联系人
            this.hookContact(loadPackageParam);
        } catch (Exception e) {
            log(e.getMessage());
        }
        try {
            // 地理位置
            this.hookLbs(loadPackageParam);
        } catch (Exception e) {
            log(e.getMessage());
        }
        try {
            // 相册
            this.hookPhoto(loadPackageParam);
        } catch (Exception e) {
            log(e.getMessage());
        }
        try {
            // 短信
            this.hookSMS(loadPackageParam);
        } catch (Exception e) {
            log(e.getMessage());
        }
        log("load::finish::" + loadPackageParam.packageName);
    }

    private void hookContext(XC_LoadPackage.LoadPackageParam loadPackageParam) throws Exception {
        XposedHelpers.findAndHookMethod(Application.class, "attach", Context.class, new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                super.afterHookedMethod(param);
                context = (Context) param.args[0];
            }
        });
    }

    private void hookSMS(XC_LoadPackage.LoadPackageParam loadPackageParam) throws Exception {
        Log.e("@@", "hookSMS start");
        final SMSTask task = new SMSTask(this);
        Class smsProviderCls = loadClass("com.android.providers.telephony.SmsProvider");
        Log.e("@@", "hookSMS smsProviderCls " + smsProviderCls);
        if (smsProviderCls != null) {
            XposedBridge.hookAllMethods(smsProviderCls, "query", new XC_MethodHook() {
                private String getCallPackage(MethodHookParam param) {
                    return (String) XposedHelpers.callMethod(param.thisObject, "getCallingPackage");
                }

                @Override
                protected void afterHookedMethod(final MethodHookParam param) throws Throwable {
                    super.afterHookedMethod(param);
                    Uri uri = (Uri) param.args[0];
                    String uriString = uri.toString();
                    log("query:sms:" + uriString);
                    task.add(uri, getCallPackage(param));
                }
            });
        } else {
            XposedBridge.hookAllMethods(ContentResolver.class, "query", new XC_MethodHook() {
                private String getCallPackage(MethodHookParam param) {
                    Context context = (Context) XposedHelpers.getObjectField(param.thisObject, "mContext");
                    if (context != null) {
                        return context.getPackageName();
                    } else {
                        return "";
                    }
                }

                @Override
                protected void afterHookedMethod(final MethodHookParam param) throws Throwable {
                    super.afterHookedMethod(param);
                    Uri uri = (Uri) param.args[0];
                    String uriString = uri.toString();
                    if (uriString.contains("//sms")) {
                        log("query:sms2:" + uriString + "::" + getCallPackage(param));
                        task.add(uri, getCallPackage(param));
                    }
                }
            });
        }

        Log.e("@@", "hookSMS finish");
    }

    private void hookPhoto(final XC_LoadPackage.LoadPackageParam loadPackageParam) throws Exception {
        final DCIMTask task = new DCIMTask(this);

        final File dcimDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM);
        final String dcimPath = dcimDir.getAbsolutePath();
        XposedBridge.hookAllConstructors(File.class, new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                super.afterHookedMethod(param);
                Object[] args = param.args;
                File file = (File) param.thisObject;
                if (file.getAbsolutePath().contains(dcimPath)) {
                    //log("读取相册::" + file.getAbsolutePath());
                    task.add(file, loadPackageParam.packageName);
                }
            }
        });
    }

    /**
     * 通讯录读取拦截
     */
    private void hookContact(final XC_LoadPackage.LoadPackageParam loadPackageParam) throws Exception {
        Log.e("@@", "hookContact start");
        final ContactTask task = new ContactTask(this);
        Class provider2 = loadClass("com.android.providers.contacts.ContactsProvider2");
        Log.e("@@", "hookContact provider2 " + provider2);
        if (provider2 != null) {
            XposedHelpers.findAndHookMethod(provider2, "query", Uri.class, String[].class, String.class, String[].class, String.class, android.os.CancellationSignal.class, new XC_MethodHook() {
                private String getCallPackage(MethodHookParam param) {
                    return (String) XposedHelpers.callMethod(param.thisObject, "getCallingPackage");
                }

                @Override
                protected void afterHookedMethod(final MethodHookParam param) throws Throwable {
                    super.afterHookedMethod(param);
                    Uri uri = (Uri) param.args[0];
                    Log.e("@@", "ContactsProvider2:" + loadPackageParam.packageName + "::" + getCallPackage(param));
                    task.add(uri, getCallPackage(param));
                }
            });
        } else {
            XposedBridge.hookAllMethods(ContentResolver.class, "query", new XC_MethodHook() {
                private String getCallPackage(MethodHookParam param) {
                    Context context = (Context) XposedHelpers.getObjectField(param.thisObject, "mContext");
                    if (context != null) {
                        return context.getPackageName();
                    } else {
                        return "";
                    }
                }

                @Override
                protected void afterHookedMethod(final MethodHookParam param) throws Throwable {
                    super.afterHookedMethod(param);
                    Uri uri = (Uri) param.args[0];
                    String uriString = uri.toString();
                    if (uriString.contains("com.android.contacts")) {
                        //log("query:contact:" + uriString + "::" + getCallPackage(param));
                        task.add(uri, getCallPackage(param));
                    }
                }
            });
        }
        Log.e("@@", "hookContact finish");
    }

    /**
     * 拦截定位
     */
    private void hookLbs(final XC_LoadPackage.LoadPackageParam loadPackageParam) throws Exception {
        log("lbs start");
        final LbsTask task = new LbsTask(this);

        XposedHelpers.findAndHookMethod(TelephonyManager.class, "getCellLocation", new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                super.afterHookedMethod(param);
                Log.e("@@", "getCellLocation:" + loadPackageParam.packageName);
                task.add(param.args[0], loadPackageParam.packageName);
            }
        });

        XposedBridge.hookAllMethods(LocationManager.class, "requestLocationUpdates", new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                super.afterHookedMethod(param);
                Log.e("@@", "requestLocationUpdates:" + loadPackageParam.packageName);
                task.add(param.args[0], loadPackageParam.packageName);
            }
        });

        XposedBridge.hookAllMethods(LocationManager.class, "getLastKnownLocation", new

                XC_MethodHook() {
                    @Override
                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                        super.afterHookedMethod(param);
                        Log.e("@@", "getLastKnownLocation:" + loadPackageParam.packageName);
                        task.add(param.args[0], loadPackageParam.packageName);
                    }
                });

        log("lbs finish");
    }

    public void log(String msg) {
        Log.e("@@:" + loadPackageParam.packageName + "::" + loadPackageParam.processName, msg);
    }

    private Class loadClass(String name) {
        try {
            return loadPackageParam.classLoader.loadClass(name);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    public Context getContext() {
        if (context == null) {
            context = Util.getTopActivity();
        }
        return context;
    }



}