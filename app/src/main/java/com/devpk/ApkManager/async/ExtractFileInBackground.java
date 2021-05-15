package com.devpk.ApkManager.async;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.devpk.ApkManager.AppInfo;
import com.devpk.ApkManager.MLManagerApplication;
import com.devpk.ApkManager.R;
import com.devpk.ApkManager.utils.UtilsApp;
import com.devpk.ApkManager.utils.UtilsDialog;

public class ExtractFileInBackground extends AsyncTask<Void, String, Boolean> {
    private Context context;
    private Activity activity;
    private AlertDialog alertDialog;
    private AppInfo appInfo;

    public ExtractFileInBackground(Context context, AlertDialog alertDialog, AppInfo appInfo) {
        this.context = context;
        this.activity = (Activity) context;
        this.appInfo = appInfo;
        this.alertDialog = alertDialog;
    }

    @Override
    protected Boolean doInBackground(Void... voids) {
        Boolean status = false;

        if (UtilsApp.checkPermissions(activity)) {

            if (!appInfo.getApk().equals(MLManagerApplication.getProPackage())) {
                status = UtilsApp.copyFile(appInfo);
            } else {
                //Do Some thing with extractMLManagerPro
                //status = UtilsApp.extractMLManagerPro(context,appInfo);
            }
        }

        return status;
    }

    @Override
    protected void onPostExecute(Boolean status) {
        super.onPostExecute(status);
        alertDialog.dismiss();
        Log.i("onPostExecute ", "" + status);
        if (status) {
            Toast.makeText(activity,
                    "" + context.getResources().getString(R.string.dialog_saved_description) + " " + appInfo.getName(),
                    Toast.LENGTH_LONG).show();
        } else {
            UtilsDialog.showTitleContent(context,
                    context.getResources().getString(R.string.dialog_extract_fail),
                    context.getResources().getString(R.string.dialog_extract_fail_description));
        }
    }
}
