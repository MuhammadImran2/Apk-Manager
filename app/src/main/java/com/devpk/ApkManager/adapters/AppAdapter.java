package com.devpk.ApkManager.adapters;

import android.app.Activity;
import android.app.ActivityOptions;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.AdaptiveIconDrawable;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.devpk.ApkManager.AppInfo;
import com.devpk.ApkManager.MLManagerApplication;
import com.devpk.ApkManager.R;
import com.devpk.ApkManager.activities.AppActivity;
import com.devpk.ApkManager.activities.MainActivity;
import com.devpk.ApkManager.async.ExtractFileInBackground;
import com.devpk.ApkManager.utils.AppPreferences;
import com.devpk.ApkManager.utils.UtilsApp;
import com.devpk.ApkManager.utils.UtilsDialog;
import com.gc.materialdesign.views.ButtonFlat;

import java.util.ArrayList;
import java.util.List;

public class AppAdapter extends RecyclerView.Adapter<AppAdapter.AppViewHolder> implements Filterable {

    // Load Settings
    private AppPreferences appPreferences;

    private Context context;
    private List<AppInfo> appList;
    private List<AppInfo> appListSearch;

    public AppAdapter(List<AppInfo> appList, Context context) {
        this.appList = appList;
        this.context = context;
        this.appPreferences = MLManagerApplication.getAppPreferences(context);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onBindViewHolder(AppViewHolder appViewHolder, int position) {
        AppInfo appInfo = appList.get(position);

        appViewHolder.vName.setText(appInfo.getName());
        appViewHolder.vApk.setText(appInfo.getApk());
        appViewHolder.vIcon.setImageDrawable(appInfo.getIcon());
        setButtonEvents(appViewHolder, appInfo);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void setButtonEvents(AppViewHolder appViewHolder, AppInfo appInfo) {
        ButtonFlat appExtract = appViewHolder.vExtract;
        ButtonFlat appShare = appViewHolder.vShare;

        final ImageView appIcon = appViewHolder.vIcon;
        final CardView cardView = appViewHolder.vCard;

        appExtract.setBackgroundColor(appPreferences.getPrimaryColorPref());
        appShare.setBackgroundColor(appPreferences.getPrimaryColorPref());

        appExtract.setOnClickListener(v -> {

            AlertDialog alertDialog = UtilsDialog.showTitleContentWithProgress(
                    context,
                    String.format(context.getResources().getString(R.string.dialog_saving),
                            appInfo.getName()),
                    context.getResources().getString(R.string.dialog_saving_description));

            new ExtractFileInBackground(context, alertDialog, appInfo).execute();

        });

        appShare.setOnClickListener(v -> {
            UtilsApp.copyFile(appInfo);
            Intent shareIntent = UtilsApp.getShareIntent(UtilsApp.getOutputFilename(appInfo));
            context.startActivity(Intent.createChooser(shareIntent,
                    String.format(context.getResources().getString(R.string.send_to), appInfo.getName())));
        });

        cardView.setOnClickListener(v -> {
            Activity activity = (Activity) context;

            Intent intent = new Intent(activity, AppActivity.class);
            intent.putExtra("app_name", appInfo.getName());
            intent.putExtra("app_apk", appInfo.getApk());
            intent.putExtra("app_version", appInfo.getVersion());
            intent.putExtra("app_source", appInfo.getSource());
            intent.putExtra("app_data", appInfo.getData());
            intent.putExtra("app_isSystem", appInfo.isSystem());

            Drawable drawable = appInfo.getIcon();
            Bitmap bitmap = getDefaultBitmap(drawable);
            intent.putExtra("app_icon", getResizedBitmap(bitmap, 216));


            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                String transitionName = context.getResources().getString(R.string.transition_app_icon);
                ActivityOptions transitionActivityOptions = ActivityOptions.makeSceneTransitionAnimation(activity, appIcon, transitionName);
                context.startActivity(intent, transitionActivityOptions.toBundle());
            } else {
                context.startActivity(intent);
                activity.overridePendingTransition(R.anim.slide_in_right, R.anim.fade_back);
            }

        });

    }

    public void clear() {
        appList.clear();
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return appList.size();
    }

    @Override
    public AppViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View appAdapterView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.app_layout, viewGroup, false);
        return new AppViewHolder(appAdapterView);
    }

    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                final FilterResults oReturn = new FilterResults();
                final List<AppInfo> results = new ArrayList<>();

                if (appListSearch == null) {
                    appListSearch = appList;
                }
                if (charSequence != null) {
                    if (appListSearch != null && appListSearch.size() > 0) {

                        for (final AppInfo appInfo : appListSearch) {
                            if (appInfo.getName().toLowerCase().contains(charSequence.toString())) {
                                results.add(appInfo);
                            }
                        }
                    }
                    oReturn.values = results;
                    oReturn.count = results.size();
                }
                return oReturn;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults results) {

                if (results.count > 0) {
                    MainActivity.setResultsMessage(false);
                } else {
                    MainActivity.setResultsMessage(true);
                }

                appList = (ArrayList<AppInfo>) results.values;
                notifyDataSetChanged();

            }
        };
    }

    public static class AppViewHolder extends RecyclerView.ViewHolder {
        protected TextView vName;
        protected TextView vApk;
        protected ImageView vIcon;
        protected ButtonFlat vExtract;
        protected ButtonFlat vShare;
        protected CardView vCard;

        public AppViewHolder(@NonNull View itemView) {
            super(itemView);
            vName = itemView.findViewById(R.id.txtName);
            vApk = itemView.findViewById(R.id.txtApk);
            vIcon = itemView.findViewById(R.id.imgIcon);
            vExtract = itemView.findViewById(R.id.btnExtract);
            vShare = itemView.findViewById(R.id.btnShare);
            vCard = itemView.findViewById(R.id.app_card);
        }
    }


    @RequiresApi(api = Build.VERSION_CODES.O)
    public Bitmap getDefaultBitmap(Drawable d) {
        if (d instanceof BitmapDrawable) {
            return ((BitmapDrawable) d).getBitmap();
        } else if ((Build.VERSION.SDK_INT >= 26) && (d instanceof AdaptiveIconDrawable)) {
            AdaptiveIconDrawable icon = ((AdaptiveIconDrawable) d);
            int w = icon.getIntrinsicWidth();
            int h = icon.getIntrinsicHeight();
            Bitmap result = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(result);
            icon.setBounds(0, 0, w, h);
            icon.draw(canvas);
            return result;
        }

        float density = context.getResources().getDisplayMetrics().density;
        int defaultWidth = (int) (48 * density);
        int defaultHeight = (int) (48 * density);
        return Bitmap.createBitmap(defaultWidth, defaultHeight, Bitmap.Config.ARGB_8888);
    }
    // transforms an Drawable object to Bitmap


    /**
     * reduces the size of the image
     *
     * @param image
     * @param maxSize
     * @return
     */
    public Bitmap getResizedBitmap(Bitmap image, int maxSize) {
        int width = image.getWidth();
        int height = image.getHeight();

        Log.i("getResizedBitmap ", " width " + width + " height " + height);

        float bitmapRatio = (float) width / (float) height;
        if (bitmapRatio > 1) {
            width = maxSize;
            height = (int) (width / bitmapRatio);
        } else {
            height = maxSize;
            width = (int) (height * bitmapRatio);
        }
        return Bitmap.createScaledBitmap(image, width, height, true);
    }

}
