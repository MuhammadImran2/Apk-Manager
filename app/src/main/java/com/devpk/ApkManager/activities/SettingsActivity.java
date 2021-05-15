package com.devpk.ApkManager.activities;

import androidx.appcompat.widget.Toolbar;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.devpk.ApkManager.MLManagerApplication;
import com.devpk.ApkManager.R;
import com.devpk.ApkManager.utils.AppPreferences;
import com.devpk.ApkManager.utils.UtilsApp;
import com.devpk.ApkManager.utils.UtilsUI;
import com.keenfin.sfcdialog.SimpleFileChooser;

import java.io.File;

import yuku.ambilwarna.widget.AmbilWarnaPreference;

public class SettingsActivity extends PreferenceActivity implements SharedPreferences.OnSharedPreferenceChangeListener
        /*, OnDirectoryChooserFragmentInteraction*/ {

    // Load Settings
    private AppPreferences appPreferences;
    private Toolbar toolbar;
    private Context context;

    private Preference prefVersion, prefLicense, prefDeleteAll, prefDefaultValues, prefNavigationBlack, prefCustomPath;
    private AmbilWarnaPreference prefPrimaryColor, prefFABColor;
    private ListPreference prefCustomFilename, prefSortMode;
    /* private DirectoryChooserFragment chooserDialog;*/
    SimpleFileChooser sfcDialog = new SimpleFileChooser();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.settings);
        this.context = this;
        this.appPreferences = MLManagerApplication.getAppPreferences(context);

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        sharedPreferences.registerOnSharedPreferenceChangeListener(this);


        // findPreference
        prefVersion = findPreference("prefVersion");
        prefLicense = findPreference("prefLicense");
        prefDeleteAll = findPreference("prefDeleteAll");
        prefDefaultValues = findPreference("prefDefaultValues");
        prefNavigationBlack = findPreference("prefNavigationBlack");
        prefCustomPath = findPreference("prefCustomPath");

        // ListPreference
        prefCustomFilename = (ListPreference) findPreference("prefCustomFilename");
        prefSortMode = (ListPreference) findPreference("prefSortMode");

        //AmbilWarnaPreference
        prefPrimaryColor = (AmbilWarnaPreference) findPreference("prefPrimaryColor");
        prefFABColor = (AmbilWarnaPreference) findPreference("prefFABColor");

        setInitialConfiguration();

        String versionName = UtilsApp.getAppVersionName(context);
        int versionCode = UtilsApp.getAppVersionCode(context);

        prefVersion.setTitle(getResources().getString(R.string.app_name) + " v " + versionName + " ( " + versionCode + " )");
        prefVersion.setOnPreferenceClickListener(preference -> {
            startActivity(new Intent(context, AboutActivity.class));
            overridePendingTransition(R.anim.slide_in_right, R.anim.fade_back);
            return false;
        });

        prefLicense.setOnPreferenceClickListener(preference -> {
            startActivity(new Intent(context, LicenseActivity.class));
            overridePendingTransition(R.anim.slide_in_right, R.anim.fade_back);
            return false;
        });


        // prefCustomFilename
        setCustomFilenameSummary();

        // prefSortMode
        setSortModeSummary();

        // prefCustomPath
        setCustomPathSummary();

        // prefDeleteAll
        prefDeleteAll.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                prefDeleteAll.setSummary(R.string.deleting);
                prefDeleteAll.setEnabled(false);
                Boolean deleteAll = UtilsApp.deleteAppFiles();
                if (deleteAll) {
                    prefDeleteAll.setSummary(R.string.deleting_done);
                } else {
                    prefDeleteAll.setSummary(R.string.deleting_error);
                }
                prefDeleteAll.setEnabled(true);
                return true;
            }
        });

        // prefDefaultValues
        prefDefaultValues.setOnPreferenceClickListener(preference -> {
            appPreferences.setPrimaryColorPref(getResources().getColor(R.color.primary));
            appPreferences.setFABColorPref(getResources().getColor(R.color.fab));
            return true;
        });

        File currentRootDirectory = Environment.getExternalStorageDirectory();

        // prefCustomPath
        prefCustomPath.setOnPreferenceClickListener(preference -> {

            sfcDialog.setOnChosenListener(new SimpleFileChooser.SimpleFileChooserListener() {
                @Override
                public void onFileChosen(File file) {
                    Toast.makeText(getApplicationContext(),
                            "Please Chose Folder Not Any Other File", Toast.LENGTH_LONG
                    ).show();
                }

                @Override
                public void onDirectoryChosen(File directory) {
                    appPreferences.setCustomPath(directory.getPath());
                    setCustomPathSummary();
                }

                @Override
                public void onCancel() {
                    sfcDialog.dismiss();
                }
            });
            sfcDialog.show(getFragmentManager(), "SimpleFileChooserDialog");
            return false;
        });
    }

    @Override
    public void setContentView(int layoutResID) {
        ViewGroup contentView = (ViewGroup) LayoutInflater.from(this).inflate(R.layout.activity_settings, new LinearLayout(this), false);
        toolbar = contentView.findViewById(R.id.toolbar);
        //TODO Toolbar should load the default style in XML (white title and back arrow), but doesn't happen
        toolbar.setTitleTextColor(getResources().getColor(R.color.white));
        toolbar.setNavigationOnClickListener(view -> onBackPressed());

        ViewGroup contentWrapper = (ViewGroup) contentView.findViewById(R.id.content_wrapper);
        LayoutInflater.from(this).inflate(layoutResID, contentWrapper, true);
        getWindow().setContentView(contentView);
    }

    private void setInitialConfiguration() {
        toolbar.setTitle(getResources().getString(R.string.action_settings));
        // Android 5.0+ devices
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            getWindow().setStatusBarColor(UtilsUI.darker(appPreferences.getPrimaryColorPref(), 0.8));
            toolbar.setBackgroundColor(appPreferences.getPrimaryColorPref());
            if (!appPreferences.getNavigationBlackPref()) {
                getWindow().setNavigationBarColor(appPreferences.getPrimaryColorPref());
            }
        }

        // Pre-Lollipop devices
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            prefPrimaryColor.setEnabled(false);
            prefNavigationBlack.setEnabled(false);
            prefNavigationBlack.setDefaultValue(true);
        }
    }

    private void setCustomFilenameSummary() {

    }

    private void setSortModeSummary() {
    }

    private void setCustomPathSummary() {
        String path = appPreferences.getCustomPath();
        if (path.equals(UtilsApp.getDefaultAppFolder().getPath())) {
            prefCustomPath.setSummary(
                    getResources().getString(R.string.button_default) + ": " + UtilsApp.getDefaultAppFolder().getPath());
        } else {
            prefCustomPath.setSummary(path);
        }
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.fade_forward, R.anim.slide_out_right);
    }

}