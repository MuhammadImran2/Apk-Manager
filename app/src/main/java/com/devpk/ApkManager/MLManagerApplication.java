package com.devpk.ApkManager;


import android.app.Application;
import android.content.Context;


import com.devpk.ApkManager.utils.AppPreferences;


public class MLManagerApplication extends Application {

    private static AppPreferences sAppPreferences;
    private static boolean isPro;

    @Override
    public void onCreate() {
        super.onCreate();
        // Load Shared Preference
        sAppPreferences = new AppPreferences(this);

    }

    public static AppPreferences getAppPreferences(Context context) {
        sAppPreferences = new AppPreferences(context);
        return sAppPreferences;
    }

    /**
     * Retrieve ML Manager Pro
     *
     * @return true for ML Manager Pro, false otherwise
     */
    public static Boolean isPro() {
        return isPro;
    }

    public static void setPro(Boolean res) {
        isPro = res;
    }

    public static String getProPackage() {
        return "com.javiersantos.mlmanagerpro";
    }

}
