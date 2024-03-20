package com.group17.home.activities;

import android.Manifest;
import android.arch.lifecycle.Observer;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.group17.home.R;
import com.group17.home.R2;
import com.group17.home.helpers.MessagesListAdapter;
import com.group17.model.entities.Message;
import com.group17.model.entities.User;
import com.group17.model.helper.BaseActivity;
import com.group17.model.helper.GlideApp;
import com.group17.model.rest.ErrorHandlerCallback;

import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.ViewById;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import permissions.dispatcher.NeedsPermission;
import permissions.dispatcher.OnNeverAskAgain;
import permissions.dispatcher.OnPermissionDenied;
import permissions.dispatcher.OnShowRationale;
import permissions.dispatcher.PermissionRequest;
import permissions.dispatcher.RuntimePermissions;
import retrofit2.Call;
import retrofit2.Response;


@RuntimePermissions
@EActivity
public class ConversationActivity extends BaseActivity {

    @Extra("partnerId")
    int partnerId = 0;

    @Extra("partnerEmail")
    String partnerEmail = null;

    @ViewById(R2.id.profile_picture_view)
    ImageView profilePicView;

    @ViewById(R2.id.user_name_view)
    TextView userNameView;

    @ViewById(R2.id.partner_picture_view)
    ImageView partnerPicView;

    @ViewById(R2.id.partner_name_view)
    TextView partnerNameView;

    @ViewById(R2.id.message_box)
    EditText messageBox;

    private MessagesListAdapter adapter = null;

    private FusedLocationProviderClient fusedLocationProvider;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_conversation);

        fusedLocationProvider = LocationServices.getFusedLocationProviderClient(this);

        ((ListView) ConversationActivity.this.findViewById(R.id.recent_conversations_view)).setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (ConversationActivity.this.adapter != null) {
                    Message message = ConversationActivity.this.adapter.getItem(position);

                    // TODO: pass to the conversation activity the partner's userId, utilize AndroidAnnotations for this
                }
            }
        });

        db.messageDao().getMessagesForConversation(partnerId).observe(this, new Observer<List<Message>>() {
            @Override
            public void onChanged(@Nullable List<Message> messages) {
                // Update UI(neka bude ListView, che niamam vreme za RecyclerView)
                // TODO: will it be called once in the beginning???

                ConversationActivity.this.adapter = new MessagesListAdapter(ConversationActivity.this, ConversationActivity.this.db, messages, partnerId);

                ((ListView) ConversationActivity.this.findViewById(R.id.recent_conversations_view)).setAdapter(ConversationActivity.this.adapter);
                adapter.notifyDataSetChanged();
            }
        });
    }

    @Click(R2.id.location_button)
    void locationButtonHandler(View backButtonView) {
        sendLocation();
    }

    @NeedsPermission({Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION})
    void sendLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        // Check if GPS is enabled
        LocationManager locationManager = (LocationManager)this.getSystemService(Context.LOCATION_SERVICE);

        if(locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) == false) {
            Toast.makeText(ConversationActivity.this, "GPS not enabled.", Toast.LENGTH_SHORT).show();
            return;
        }

        Task<Location> location = fusedLocationProvider.getLastLocation();
        location.addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if(location != null) {
                    api.sendMessage(
                            partnerId,
                            "Location: " + Double.toString(location.getLatitude()) + ", " + Double.toString(location.getLongitude())
                    ).enqueue(new ErrorHandlerCallback(ConversationActivity.this) {
                        @Override
                        public void onResponse(Call<String> call, Response<String> response) {
                            super.onResponse(call, response);
                            if(response.isSuccessful()) {
                                ConversationActivity.this.messageBox.setText("");
                            }
                        }

                        @Override
                        public void onFailure(Call<String> call, Throwable t) {
                            super.onFailure(call, t);
                        }
                    });
                } else {
                    Toast.makeText(ConversationActivity.this, "Location is unavailable at the moment.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @OnShowRationale({Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION})
    void showRationaleForLocationPermissions(final PermissionRequest request) {
        new AlertDialog.Builder(this)
                .setMessage("Coarse and fine location permissions are required.")
                .setPositiveButton("Allow", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        request.proceed();
                    }
                })
                .setNegativeButton("Deny", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        request.cancel();
                    }
                })
                .show();
    }

    @OnPermissionDenied({Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION})
    void showDeniedForLocationPermissions() {
        Toast.makeText(this, "Permission denied.", Toast.LENGTH_SHORT).show();
    }

    @OnNeverAskAgain(Manifest.permission.CAMERA)
    void showNeverAskForLocationPermissions() {
        Toast.makeText(this, "You can toggle your permissions from your Android Settings.", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onResume() {
        super.onResume();

        // TOVA E ZA UPDATING NA SAMIA DATA!!!, inache sas sigurnost ima cachenata versia offline

        showLoadingDialog("Loading user data...");

        // Get the user email from the shared preferences
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        final String email = preferences.getString("email", ""); // Must always be set!!!

        // Get most up to date data from the internet and update the database cache
        api.getUserInfo(
                partnerEmail
        ).enqueue(new ErrorHandlerCallback(this) {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                super.onResponse(call, response);

                if(response.isSuccessful()) {
                    // Parse the given user info
                    JSONObject responseJSONObject = null;
                    User user = new User();
                    try {
                        responseJSONObject = new JSONObject(response.body().toString());

                        user.user_id = Integer.parseInt(responseJSONObject.getString("user_id"));
                        user.email = responseJSONObject.getString("email");
                        user.name = responseJSONObject.getString("name");
                        user.occupation = responseJSONObject.getString("occupation");
                        user.birthday = responseJSONObject.getString("birthday");
                        user.interests = responseJSONObject.getString("interests");
                        user.introduction = responseJSONObject.getString("introduction");
                        user.profilePicName = responseJSONObject.getString("profile_pic_name");
                        user.matchPicName = responseJSONObject.getString("match_pic_name");
                        user.faceId = responseJSONObject.getString("face_id");
                    } catch (JSONException e) {
                        // MUST NEVER BE REACHED!!!
                        e.printStackTrace();
                    }

                    // Insert/update the user info into the local database
                    db.userDao().insert(user);

                    // Set the profile picture image with Glide
                    GlideApp
                            .with(ConversationActivity.this)
                            .load("http://206.189.125.23/auth/picture/" + user.profilePicName)
                            .fitCenter()
                            .into(partnerPicView);

                    // Set user name
                    partnerNameView.setText(user.name);

                    hideLoadingDialog();

                } else {
                    // MUST NEVER REACH THIS LINE!!!
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                super.onFailure(call, t);

                // Try to get user_info from the local database using the email
                User user = db.userDao().getUserByEmail(partnerEmail);

                if(user == null) {
                    // Display the error message and then finish the activity in the handler
                    ConversationActivity.this.showTerminationDialog("Insufficient information", "The application hasn't had an opportunity to cache your information and cannot retrieve it from the internet. The application will close now.");
                } else {
                    // Set the profile picture image with Glide
                    GlideApp
                            .with(ConversationActivity.this)
                            .load("http://206.189.125.23/auth/picture/" + user.profilePicName)
                            .fitCenter()
                            .into(partnerPicView);

                    // Set user name
                    partnerNameView.setText(user.name);
                }

                hideLoadingDialog();
            }
        });

        api.getUserInfo(
                email
        ).enqueue(new ErrorHandlerCallback(this) {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                super.onResponse(call, response);

                if(response.isSuccessful()) {
                    // Parse the given user info
                    JSONObject responseJSONObject = null;
                    User user = new User();
                    try {
                        responseJSONObject = new JSONObject(response.body().toString());

                        user.user_id = Integer.parseInt(responseJSONObject.getString("user_id"));
                        user.email = responseJSONObject.getString("email");
                        user.name = responseJSONObject.getString("name");
                        user.occupation = responseJSONObject.getString("occupation");
                        user.birthday = responseJSONObject.getString("birthday");
                        user.interests = responseJSONObject.getString("interests");
                        user.introduction = responseJSONObject.getString("introduction");
                        user.profilePicName = responseJSONObject.getString("profile_pic_name");
                        user.matchPicName = responseJSONObject.getString("match_pic_name");
                        user.faceId = responseJSONObject.getString("face_id");
                    } catch (JSONException e) {
                        // MUST NEVER BE REACHED!!!
                        e.printStackTrace();
                    }

                    // Insert/update the user info into the local database
                    db.userDao().insert(user);

                    // Set the profile picture image with Glide
                    GlideApp
                            .with(ConversationActivity.this)
                            .load("http://206.189.125.23/auth/picture/" + user.profilePicName)
                            .fitCenter()
                            .into(profilePicView);

                    // Set user name
                    userNameView.setText(user.name);

                    hideLoadingDialog();

                } else {
                    // MUST NEVER REACH THIS LINE!!!
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                super.onFailure(call, t);

                // Try to get user_info from the local database using the email
                User user = db.userDao().getUserByEmail(email);

                if(user == null) {
                    // Display the error message and then finish the activity in the handler
                    ConversationActivity.this.showTerminationDialog("Insufficient information", "The application hasn't had an opportunity to cache your information and cannot retrieve it from the internet. The application will close now.");
                } else {
                    // Set the profile picture image with Glide
                    GlideApp
                            .with(ConversationActivity.this)
                            .load("http://206.189.125.23/auth/picture/" + user.profilePicName)
                            .fitCenter()
                            .into(profilePicView);

                    // Set user name
                    userNameView.setText(user.name);
                }

                hideLoadingDialog();
            }
        });
    }

    public void showTerminationDialog(String title, String content) {
        LayoutInflater inflater = LayoutInflater.from(this);
        View termsOfServiceLayout = inflater.inflate(R.layout.message_box, null);

        TextView messageView = termsOfServiceLayout.findViewById(R.id.message_view);
        messageView.setText(content);

        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        alertDialog.setTitle(title);
        alertDialog.setView(termsOfServiceLayout);
        alertDialog.setNegativeButton("Close", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                ConversationActivity.this.finish();
            }
        });
        AlertDialog alert = alertDialog.create();
        alert.show();
    }

    @Click(R2.id.back_button)
    void backButtonHandler(View backButtonView) {
        this.finish();
    }

    @Click(R2.id.send_button)
    void sendButtonHandler(View backButtonView) {
        api.sendMessage(
                partnerId,
                messageBox.getText().toString()
        ).enqueue(new ErrorHandlerCallback(this) {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                super.onResponse(call, response);
                if(response.isSuccessful()) {
                    ConversationActivity.this.messageBox.setText("");
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                super.onFailure(call, t);
            }
        });
    }

    @Click(R2.id.menu_button)
    void menuButtonClickHandler(View backButtonView) {
        Intent intent = null;
        try {
            intent = new Intent(ConversationActivity.this, Class.forName("com.group17.settings.activities.SettingsActivity_"));
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        startActivity(intent);
    }
}
