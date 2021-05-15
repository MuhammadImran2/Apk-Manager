package com.devpk.ApkManager.utils;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;


import androidx.core.content.ContextCompat;

import com.devpk.ApkManager.AppInfo;
import com.devpk.ApkManager.MLManagerApplication;
import com.devpk.ApkManager.R;


import org.apache.commons.io.FileUtils;

import java.io.File;
import java.util.Set;

public class UtilsApp {

    private static Context context;

    public UtilsApp(Context context) {
        this.context = context;
    }

    private static final int MY_PERMISSIONS_REQUEST_WRITE_READ = 1;

    /**
     * Default folder where APKs will be saved
     *
     * @return File with the path
     */
    public static File getDefaultAppFolder() {
        return new File(Environment.getExternalStorageDirectory() + "/MLManager");
    }

    /**
     * Custom folder where APKs will be saved
     *
     * @return File with the path
     */
    public static File getAppFolder(Context context) {
        AppPreferences appPreferences = MLManagerApplication.getAppPreferences(context);
        return new File(appPreferences.getCustomPath());
    }

    public static Boolean copyFile(AppInfo appInfo) {
        Boolean res = false;

        File initialFile = new File(appInfo.getSource());
        File finalFile = getOutputFilename(appInfo);

        try {
            FileUtils.copyFile(initialFile, finalFile);
            res = true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return res;
    }

    /**
     * Retrieve the name of the extracted APK
     *
     * @param appInfo AppInfo
     * @return String with the output name
     */
    private static String getAPKFilename(AppInfo appInfo) {
        AppPreferences appPreferences = MLManagerApplication.getAppPreferences(context);
        String res;

        switch (appPreferences.getCustomFilename()) {
            case "1":
                res = appInfo.getApk() + "_" + appInfo.getVersion();
                break;
            case "2":
                res = appInfo.getName() + "_" + appInfo.getVersion();
                break;
            case "4":
                res = appInfo.getName();
                break;
            default:
                res = appInfo.getApk();
                break;
        }

        return res;
    }

    /**
     * Retrieve the name of the extracted APK with the path
     *
     * @param appInfo AppInfo
     * @return File with the path and output name
     */
    public static File getOutputFilename(AppInfo appInfo) {
        return new File(getAppFolder(context).getPath() + "/" + getAPKFilename(appInfo) + ".apk");
    }

    public static boolean checkPermissions(Activity activity) {
        Boolean res = false;

        if (ContextCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            activity.requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE}, MY_PERMISSIONS_REQUEST_WRITE_READ);
        } else {
            res = true;
        }

        return res;
    }


    /**
     * Retrieve if an app has been marked as favorite
     *
     * @param apk          App to check
     * @param favoriteApps Set with apps
     * @return true if the app is marked as favorite, false otherwise
     */
    public static boolean isAppFavorite(String apk, Set<String> favoriteApps) {
        Boolean res = false;
        if (favoriteApps.contains(apk)) {
            res = true;
        }
        return res;
    }

    public static Drawable getIconFromCache(Context context, AppInfo appInfo) {
        Drawable drawable;

        try {
            File fileUri = new File(context.getCacheDir(), appInfo.getApk());
            Bitmap bitmap = BitmapFactory.decodeFile(fileUri.getPath());
            drawable = new BitmapDrawable(context.getResources(), bitmap);
        } catch (Exception e) {
            e.printStackTrace();
            drawable = context.getResources().getDrawable(R.drawable.ic_android);
        }
        return drawable;
    }


    public static Boolean extractMLManagerPro(Context context, AppInfo appInfo) {
        return false;
    }

    public static Intent getShareIntent(File outputFilename) {
        Log.i("Checking ", "" + outputFilename);
        Intent intent = new Intent();
        intent.setType(Intent.ACTION_SEND);
        intent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(outputFilename));
        intent.setType("application/vnd.android.package-archive");
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        return intent;
    }

    /**
     * Opens Google Play if installed, if not opens browser
     *
     * @param context Context
     * @param id      PackageName on Google Play
     */
    public static void goToGooglePlay(Context context, String id) {
        try {
            context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + id)));
        } catch (Exception e) {
            e.printStackTrace();
            context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + id)));
        }
    }

    /**
     * Opens Google Plus
     *
     * @param context Context
     * @param id      Name on Google Play
     */
    public static void goToGooglePlus(Context context, String id) {
        context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://plus.google.com/" + id)));
    }

    /**
     * Retrieve your own app version
     *
     * @param context Context
     * @return String with the app version
     */
    public static String getAppVersionName(Context context) {
        String res = "0.0.0.0";
        try {
            res = context.getPackageManager().getPackageInfo(context.getPackageName(), 0).packageName;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return res;
    }

    /**
     * Retrieve your own app version code
     *
     * @param context Context
     * @return int with the app version code
     */
    public static int getAppVersionCode(Context context) {
        int res = 0;
        try {
            res = context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionCode;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return res;
    }

    /**
     * Delete all the extracted APKs
     *
     * @return true if all files have been deleted, false otherwise
     */

    public static Boolean deleteAppFiles() {
        Boolean res = false;
        File file = getDefaultAppFolder();
        if (file.exists() && file.isDirectory()) {
            File[] files = file.listFiles();
            for (File file1 : files) {
                file1.delete();
            }
            if (file.listFiles().length == 0) {
                res = true;
            }
        }
        return res;
    }
}
