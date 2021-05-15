package com.devpk.ApkManager.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import com.devpk.ApkManager.MLManagerApplication;
import com.devpk.ApkManager.R;
import com.devpk.ApkManager.utils.AppPreferences;
import com.devpk.ApkManager.utils.UtilsApp;
import com.devpk.ApkManager.utils.UtilsUI;

public class AboutActivity extends AppCompatActivity {
    // Load Settings
    AppPreferences appPreferences;

    // About variables
    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        this.context = this;
        this.appPreferences = MLManagerApplication.getAppPreferences(context);


        setInitialConfiguration();
        setScreenElements();
    }

    private void setInitialConfiguration() {

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(R.string.action_about);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            getWindow().setStatusBarColor(UtilsUI.darker(appPreferences.getPrimaryColorPref(), 0.8));
            if (!appPreferences.getNavigationBlackPref()) {
                getWindow().setNavigationBarColor(appPreferences.getPrimaryColorPref());
            }
        }

    }

    @SuppressLint("SetTextI18n")
    private void setScreenElements() {
        TextView header = findViewById(R.id.header);
        TextView appNameVersion = findViewById(R.id.app_name);
        CardView about_1 = findViewById(R.id.about_1);
        CardView about_2 = findViewById(R.id.about_2);
        CardView about_googleplay = findViewById(R.id.about_googleplay);
        CardView about_googleplus = findViewById(R.id.about_googleplus);

        header.setBackgroundColor(appPreferences.getPrimaryColorPref());
        appNameVersion.setText(getResources().getString(R.string.app_name) +
                " " + UtilsApp.getAppVersionName(getApplicationContext()) + " \"" + getResources().getString(R.string.app_codename) + "\"");

        about_1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UtilsApp.goToGooglePlus(context, "+JavierSantos");
            }
        });

        about_2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UtilsApp.goToGooglePlus(context, "+javitoro95");
            }
        });


        about_googleplay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UtilsApp.goToGooglePlay(context, context.getPackageName());
            }
        });


        about_googleplus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UtilsApp.goToGooglePlus(context, "communities/111960842500303983487");
            }
        });


    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.fade_forward, R.anim.slide_out_right);
    }
}






