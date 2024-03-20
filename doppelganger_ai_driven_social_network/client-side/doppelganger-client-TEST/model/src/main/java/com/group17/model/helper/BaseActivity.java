package com.group17.model.helper;

import android.app.ActivityManager;
import android.app.ProgressDialog;
import android.arch.persistence.db.SupportSQLiteDatabase;
import android.arch.persistence.room.BuildConfig;
import android.arch.persistence.room.Room;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.inputmethodservice.InputMethodService;
import android.os.Bundle;
import android.os.IBinder;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import com.group17.model.R;
import com.group17.model.SyncService;
import com.group17.model.db.migrations.DoppelgangerDatabase;
import com.group17.model.rest.IDoppelgangerAPI;
import com.group17.model.rest.SessionIdCapturer;
import com.group17.model.rest.SessionIdInjector;

import java.lang.reflect.Method;
import java.net.InetAddress;
import java.util.HashMap;

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.scalars.ScalarsConverterFactory;
import us.feras.mdv.MarkdownView;

public class BaseActivity extends AppCompatActivity {

    protected IDoppelgangerAPI api = null;
    protected DoppelgangerDatabase db = null;
    protected Call<String> currentRequest = null;
    protected InputMethodManager inputMethodManager;
    protected ProgressDialog loadingDialog;

    @Override
    protected void onStop() {
        super.onStop();
        // Cancel current HTTP request
        if(currentRequest != null) {
            currentRequest.cancel();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(isServiceRunning(SyncService.class) == false) {
            // Start the synchronization service
            Intent intent = new Intent(this, SyncService.class);
            startService(intent);
        }
    }

    private boolean isServiceRunning(Class<?> serviceClass) {
        ActivityManager activityManager = (ActivityManager)getSystemService(Context.ACTIVITY_SERVICE);

        for (ActivityManager.RunningServiceInfo currentService: activityManager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(currentService.service.getClassName())) {
                return true;
            }
        }

        return false;
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Fix layout scaling
        DisplayMetrics displayMetrics = this.getResources().getDisplayMetrics();
        Configuration config = this.getResources().getConfiguration();
        config.densityDpi = 160;
        this.getResources().updateConfiguration(config, displayMetrics);

        // Get the Input Method Service
        inputMethodManager = (InputMethodManager) this.getSystemService(INPUT_METHOD_SERVICE);

        // Get Retrofit client
        this.api = DoppelgangerApplication.getApp().api;

        // Get the database
        this.db = DoppelgangerApplication.getApp().db;
    }



    public void showLoadingDialog(String message) {
        // Fix layout scaling
        DisplayMetrics displayMetrics = this.getResources().getDisplayMetrics();
        Configuration config = this.getResources().getConfiguration();
        config.densityDpi = 213;
        this.getResources().updateConfiguration(config, displayMetrics);

        this.loadingDialog = ProgressDialog.show(
                this, "",
                message, true
        );

        // Fix layout scaling
        config.densityDpi = 160;
        this.getResources().updateConfiguration(config, displayMetrics);
    }

    public void hideLoadingDialog(){
        this.loadingDialog.dismiss();
    }

    public void hideKeyboard(IBinder token) {
        inputMethodManager.hideSoftInputFromWindow(token, 0);
    }

    public void showMarkdownAlertDialog(String title, String assetFileName) {
        // Fix layout scaling
        DisplayMetrics displayMetrics = this.getResources().getDisplayMetrics();
        Configuration config = this.getResources().getConfiguration();
        config.densityDpi = 213;
        this.getResources().updateConfiguration(config, displayMetrics);

        LayoutInflater inflater = LayoutInflater.from(this);
        View termsOfServiceLayout = inflater.inflate(R.layout.terms_of_service, null);

        MarkdownView markdownView = termsOfServiceLayout.findViewById(R.id.terms_of_service_view);
        markdownView.loadMarkdownFile("file:///android_asset/" + assetFileName);

        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        alertDialog.setTitle(title);
        alertDialog.setView(termsOfServiceLayout);
        alertDialog.setNegativeButton("Close", null);
        AlertDialog alert = alertDialog.create();
        alert.show();

        // Fix layout scaling
        config.densityDpi = 160;
        this.getResources().updateConfiguration(config, displayMetrics);
    }

    public void showError(String error) {
        Toast.makeText(this, error, Toast.LENGTH_SHORT).show();
    }
}
