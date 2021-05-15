package com.devpk.ApkManager.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.devpk.ApkManager.AppInfo;
import com.devpk.ApkManager.MLManagerApplication;
import com.devpk.ApkManager.R;
import com.devpk.ApkManager.async.ExtractFileInBackground;
import com.devpk.ApkManager.utils.AppPreferences;
import com.devpk.ApkManager.utils.UtilsApp;
import com.devpk.ApkManager.utils.UtilsDialog;
import com.devpk.ApkManager.utils.UtilsUI;
import com.getbase.floatingactionbutton.FloatingActionButton;
import com.getbase.floatingactionbutton.FloatingActionsMenu;

import java.util.Set;

public class AppActivity extends AppCompatActivity {

    //Load Settings
    private AppPreferences appPreferences;

    //General variables
    private AppInfo appInfo;
    private Set<String> appsFavorite;
    private Set<String> appsHidden;

    // Configuration variables
    private int UNINSTALL_REQUEST_CODE = 1;
    private Context context;
    private Activity activity;
    private MenuItem item_favorite;

    // UI variables
    private FloatingActionsMenu floatingActionsMenu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app);
        this.context = this;
        this.activity = (Activity) context;
        this.appPreferences = MLManagerApplication.getAppPreferences(context);

        getInitialConfiguration();
        setInitialConfiguration();
        setScreenElements();
    }


    private void setInitialConfiguration() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(null);
            getSupportActionBar().setHomeButtonEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            getWindow().setStatusBarColor(UtilsUI.darker(appPreferences.getPrimaryColorPref(), 0.8));
            toolbar.setBackgroundColor(appPreferences.getPrimaryColorPref());
            if (!appPreferences.getNavigationBlackPref()) {
                getWindow().setNavigationBarColor(appPreferences.getPrimaryColorPref());
            }
        }

    }

    private void setScreenElements() {
        TextView header = (TextView) findViewById(R.id.header);
        ImageView icon = (ImageView) findViewById(R.id.app_icon);
        ImageView icon_googleplay = (ImageView) findViewById(R.id.app_googleplay);
        TextView name = (TextView) findViewById(R.id.app_name);
        TextView version = (TextView) findViewById(R.id.app_version);
        TextView apk = findViewById(R.id.app_apk);
        CardView googleplay = findViewById(R.id.id_card);
        CardView start = findViewById(R.id.start_card);
        CardView extract = findViewById(R.id.extract_card);
        CardView uninstall = findViewById(R.id.uninstall_card);
        CardView cache = findViewById(R.id.cache_card);
        CardView clearData = findViewById(R.id.clear_data_card);
        floatingActionsMenu = (FloatingActionsMenu) findViewById(R.id.fab);
        FloatingActionButton fab_share = findViewById(R.id.fab_a);
        FloatingActionButton fab_hide = findViewById(R.id.fab_b);
        FloatingActionButton fab_buy = findViewById(R.id.fab_buy);

        icon.setImageDrawable(appInfo.getIcon());
        name.setText(appInfo.getName());
        apk.setText(appInfo.getApk());
        version.setText(appInfo.getVersion());

        // Configure Colors
        header.setBackgroundColor(appPreferences.getPrimaryColorPref());
        fab_share.setColorNormal(appPreferences.getFABColorPref());
        fab_share.setColorPressed(UtilsUI.darker(appPreferences.getFABColorPref(), 0.8));
        fab_hide.setColorNormal(appPreferences.getFABColorPref());
        fab_hide.setColorPressed(UtilsUI.darker(appPreferences.getFABColorPref(), 0.8));
        fab_buy.setColorNormal(appPreferences.getFABColorPref());
        fab_buy.setColorPressed(UtilsUI.darker(appPreferences.getFABColorPref(), 0.8));



        // CardView
        if (appInfo.isSystem()) {
            icon_googleplay.setVisibility(View.GONE);
            start.setVisibility(View.GONE);
        } else {

            googleplay.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    UtilsApp.goToGooglePlay(context, appInfo.getApk());
                }
            });

            googleplay.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    ClipData clipData;
                    ClipboardManager clipboardManager = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
                    clipData = ClipData.newPlainText("text", appInfo.getApk());
                    clipboardManager.setPrimaryClip(clipData);
                    Toast.makeText(getApplicationContext(),
                            "" + context.getResources().getString(R.string.copied_clipboard)
                            , Toast.LENGTH_SHORT
                    ).show();
                    return false;
                }
            });

            start.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        Intent intent = getPackageManager().getLaunchIntentForPackage(appInfo.getApk());
                        startActivity(intent);
                    } catch (Exception e) {
                        e.printStackTrace();
                        Toast.makeText(getApplicationContext(), ""
                                + String.format(context.getString(R.string.dialog_cannot_open),
                                appInfo.getName()), Toast.LENGTH_SHORT)
                                .show();
                    }
                }
            });

            uninstall.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(Intent.ACTION_UNINSTALL_PACKAGE);
                    intent.setData(Uri.parse("package:" + appInfo.getApk()));
                    intent.putExtra(Intent.EXTRA_RETURN_RESULT, true);
                    startActivityForResult(intent, UNINSTALL_REQUEST_CODE);
                }
            });

        }


        extract.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog alertDialog = UtilsDialog.showTitleContentWithProgress(context
                        , String.format(getResources().getString(R.string.dialog_saving), appInfo.getName()),
                        getResources().getString(R.string.dialog_saving_description));
                new ExtractFileInBackground(context, alertDialog, appInfo).execute();
            }
        });

        // FAB (Share)
        fab_share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UtilsApp.copyFile(appInfo);
                Intent shareIntent = UtilsApp.getShareIntent(UtilsApp.getOutputFilename(appInfo));
                startActivity(Intent.createChooser(shareIntent,
                        String.format(getResources().getString(R.string.send_to),
                                appInfo.getName())));
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            Log.i("App", "OK");
            Intent intent = new Intent(this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            finish();
            startActivity(intent);
        } else if (resultCode == RESULT_CANCELED) {
            Log.i("App", "CANCEL");
        }
    }

    private void getInitialConfiguration() {
        String appName = getIntent().getStringExtra("app_name");
        String appApk = getIntent().getStringExtra("app_apk");
        String appVersion = getIntent().getStringExtra("app_version");
        String appSource = getIntent().getStringExtra("app_source");
        String appData = getIntent().getStringExtra("app_data");
        Bitmap bitmap = getIntent().getParcelableExtra("app_icon");
        Drawable appIcon = new BitmapDrawable(getResources(), bitmap);
        Boolean appIsSystem = getIntent().getExtras().getBoolean("app_isSystem");

        appInfo = new AppInfo(appName, appApk, appVersion, appSource, appData, appIcon, appIsSystem);
        appsFavorite = appPreferences.getFavoriteApps();
        appsHidden = appPreferences.getHiddenApps();
    }


    @Override
    public void onBackPressed() {
        if (floatingActionsMenu.isExpanded()){
            floatingActionsMenu.collapse();
        }else {
            super.onBackPressed();
            overridePendingTransition(R.anim.fade_forward, R.anim.slide_out_right);
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_app, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        return super.onOptionsItemSelected(item);
    }
}