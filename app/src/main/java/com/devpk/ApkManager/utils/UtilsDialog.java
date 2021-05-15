package com.devpk.ApkManager.utils;

import android.app.AlertDialog;
import android.content.Context;

public class UtilsDialog {
    public static void showTitleContent(Context context, String title, String content) {

        AlertDialog.Builder builder = new AlertDialog.Builder(context)
                .setTitle(title)
                .setMessage(content)
                .setCancelable(true)
                .setPositiveButton(android.R.string.ok, (dialog, which) -> {
                    dialog.cancel();
                });

        builder.show();
    }

    public static AlertDialog showTitleContentWithProgress(Context context, String title, String content) {
        AlertDialog alertDialog = new AlertDialog.Builder(context).create();
        alertDialog.setTitle(title);
        alertDialog.setMessage(content);
        alertDialog.setCancelable(true);
        alertDialog.show();
        return alertDialog;
    }

    /**
     * Show Snackbar
     * @param activity Activity
     * @param text Text of the Snackbar
     * @param buttonText Button text of the Snackbar
     * @param file File to remove if style == 1
     * @param style 1 for extracted APKs, 2 display without button and 3 for hidden apps
     * @return Snackbar to show
     */
   /* public static SnackBar showSnackbar(Activity activity, String text, String buttonText, final File file, Integer style) {

        return ;
    }*/
}
