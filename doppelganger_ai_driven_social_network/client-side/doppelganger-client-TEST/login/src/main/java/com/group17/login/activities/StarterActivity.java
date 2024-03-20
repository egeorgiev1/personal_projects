package com.group17.login.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.group17.home.activities.HomeActivity_;
import com.group17.login.R;
import com.group17.model.SyncService;
import com.group17.model.helper.BaseActivity;
import com.group17.setup.activities.SetupActivity;
import com.group17.setup.activities.SetupActivity_;

public class StarterActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = null;

        // Get the session id from the shared preferences
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        final String email = preferences.getString("email", ""); // Must always be set!!!

        // If the shared preferences are not set then the user is not logged in
        intent = null;
        if(email.length() == 0) {
            intent = new Intent(this, LoginActivity_.class);
        } else if(db.userDao().getUserByEmail(email) == null) { // If the database contains no info of the current user then he's not setup
            intent = new Intent(this, SetupActivity_.class);
        } else { // The user is logged-in and fully setup
            intent = new Intent(this, HomeActivity_.class);
        }

        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}
