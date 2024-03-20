package com.group17.settings.activities;

import android.Manifest;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.group17.model.entities.User;
import com.group17.model.helper.BaseActivity;
import com.group17.model.helper.GlideApp;
import com.group17.model.rest.ErrorHandlerCallback;
import com.group17.settings.R;
import com.group17.settings.R2;
import com.group17.settings.helpers.ImagePickerNormalizationHandler;

import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.FocusChange;
import org.androidannotations.annotations.InstanceState;
import org.androidannotations.annotations.ViewById;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Set;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import permissions.dispatcher.NeedsPermission;
import permissions.dispatcher.OnNeverAskAgain;
import permissions.dispatcher.OnPermissionDenied;
import permissions.dispatcher.OnShowRationale;
import permissions.dispatcher.PermissionRequest;
import permissions.dispatcher.RuntimePermissions;
import pl.aprilapps.easyphotopicker.DefaultCallback;
import pl.aprilapps.easyphotopicker.EasyImage;
import retrofit2.Call;
import retrofit2.Response;

@RuntimePermissions
@EActivity
public class SettingsActivity extends BaseActivity {

    @ViewById(R2.id.name_input)
    EditText nameInput;

    @ViewById(R2.id.occupation_input)
    EditText occupationView;

    @ViewById(R2.id.birthday_input)
    EditText birthdayInput;

    @ViewById(R2.id.interests_input)
    EditText interestsInput;

    @ViewById(R2.id.introduction_input)
    EditText introductionInput;

    @ViewById(R2.id.profile_picture_view)
    ImageView profilePictureView;

    @ViewById(R2.id.matchmaking_picture_view)
    ImageView matchPictureView;

    @ViewById(R2.id.manage_account_label)
    TextView manageAccountLabel;

    @ViewById(R2.id.delete_account_button)
    Button deleteAccountButton;

    @ViewById(R2.id.logout_button)
    Button logoutButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        // Make birthday input non-editable
        birthdayInput.setKeyListener(null);

        if(profilePicture != null) {
            // Set profile image view
            SettingsActivity.this.profilePictureView.setImageBitmap(BitmapFactory.decodeByteArray(profilePicture, 0, profilePicture.length));
        }

        if(matchPicture != null) {
            // Set profile image view
            SettingsActivity.this.matchPictureView.setImageBitmap(BitmapFactory.decodeByteArray(matchPicture, 0, matchPicture.length));
        }

        // Load data for the current user. if you can't load all data then give an error toast and finish the activity
        showLoadingDialog("Loading user data...");

        // Get the user email from the shared preferences
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        final String email = preferences.getString("email", ""); // Must always be set!!!

        // Get most up to date data from the internet and update the database cache
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

                    // Set the profile and match image with Glide
                    if(profilePicture == null) {
                        GlideApp
                                .with(SettingsActivity.this)
                                .load("http://206.189.125.23/auth/picture/" + user.profilePicName)
                                .fitCenter()
                                .into(profilePictureView);
                    }

                    if(matchPicture == null) {
                        GlideApp
                                .with(SettingsActivity.this)
                                .load("http://206.189.125.23/auth/picture/" + user.matchPicName)
                                .fitCenter()
                                .into(matchPictureView);
                    }

                    // Set user name
                    nameInput.setText(user.name);
                    occupationView.setText(user.occupation);
                    birthdayInput.setText(user.birthday);
                    interestsInput.setText(user.interests);
                    introductionInput.setText(user.introduction);

                    hideLoadingDialog();

                } else {
                    // MUST NEVER REACH THIS LINE!!!
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                super.onFailure(call, t);
                // No internet connection toast is gonna be displayed
                hideLoadingDialog();
                finish();
            }
        });

        // FOR TESTING PURPOSES:
//        nameInput.setText("Emil");
//        occupationView.setText("occupation");
//        birthdayInput.setText("1998-01-23");
//        interestsInput.setText("interests");
//        introductionInput.setText("Introduction blabla");
    }

    @InstanceState
    boolean isProfilePicture = true;

    DefaultCallback imagePickerNormalizationHandler = null;

    @InstanceState
    byte[] profilePicture = null;

    @InstanceState
    byte[] matchPicture = null;

    // Handle photo taking result
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(isProfilePicture == true) {
            // Set handler for a profile picture
            imagePickerNormalizationHandler = new ImagePickerNormalizationHandler(SettingsActivity.this) {
                @Override
                public void onImagePicked(File imageFile, EasyImage.ImageSource source, int type) {
                    super.onImagePicked(imageFile, source, type);

                    // Set profile image view
                    SettingsActivity.this.profilePictureView.setImageBitmap(BitmapFactory.decodeByteArray(outputImage, 0, outputImage.length));
                    // Set profile image byte array
                    SettingsActivity.this.profilePicture = outputImage;
                }
            };
        } else {
            // Set handler for a matchmaking picture
            imagePickerNormalizationHandler = new ImagePickerNormalizationHandler(SettingsActivity.this) {
                @Override
                public void onImagePicked(File imageFile, EasyImage.ImageSource source, int type) {
                    super.onImagePicked(imageFile, source, type);

                    // Set profile image view
                    SettingsActivity.this.matchPictureView.setImageBitmap(BitmapFactory.decodeByteArray(outputImage, 0, outputImage.length));
                    // Set profile image byte array
                    SettingsActivity.this.matchPicture = outputImage;
                }
            };
        }

        EasyImage.handleActivityResult(requestCode, resultCode, data, this, imagePickerNormalizationHandler);
    }

    @NeedsPermission({Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE})
    @Click(R2.id.profile_picture_take_photo_button)
    void profilePictureHandler(View buttonView) {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this, "Permission denied. Check your settings.", Toast.LENGTH_SHORT).show();
            return;
        }

        isProfilePicture = true;
        profilePicture = null; // So that we don't pass the Bundle limit

        // Take image with the camera
        EasyImage.openCamera(SettingsActivity.this, 0);
    }

    @NeedsPermission({Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE})
    @Click(R2.id.match_picture_take_photo_button)
    void matchPictureHandler(View buttonView) {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this, "Permission denied. Check your settings.", Toast.LENGTH_SHORT).show();
            return;
        }

        isProfilePicture = false;
        matchPicture = null; // So that we don't pass the Bundle limit

        // Take image with the camera
        EasyImage.openCamera(SettingsActivity.this, 0);
    }

    @OnShowRationale({Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE})
    void showRationaleForLocationPermissions(final PermissionRequest request) {
        new AlertDialog.Builder(this)
                .setMessage("Camera and external storage write permissions are required.")
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

    @OnPermissionDenied({Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE})
    void showDeniedForLocationPermissions() {
        Toast.makeText(this, "Permission denied.", Toast.LENGTH_SHORT).show();
    }

    @OnNeverAskAgain({Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE})
    void showNeverAskForLocationPermissions() {
        Toast.makeText(this, "You can toggle your permissions from your Android Settings.", Toast.LENGTH_SHORT).show();
    }

    public void confirmationDialog(String title, String content, DialogInterface.OnClickListener handler) {
        // Fix layout scaling
        DisplayMetrics displayMetrics = this.getResources().getDisplayMetrics();
        Configuration config = this.getResources().getConfiguration();
        config.densityDpi = 213;
        this.getResources().updateConfiguration(config, displayMetrics);

        LayoutInflater inflater = LayoutInflater.from(this);
        View termsOfServiceLayout = inflater.inflate(R.layout.message_box, null);

        TextView messageView = termsOfServiceLayout.findViewById(R.id.message_view);
        messageView.setText(content);

        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        alertDialog.setTitle(title);
        alertDialog.setView(termsOfServiceLayout);
        alertDialog.setPositiveButton("Yes", handler);
        alertDialog.setNegativeButton("No", null);
        AlertDialog alert = alertDialog.create();
        alert.show();

        // Fix layout scaling
        config.densityDpi = 160;
        this.getResources().updateConfiguration(config, displayMetrics);
    }

    @Click(R2.id.logout_button)
    void logoutHandler(View buttonView) {
        // Show "are you sure" dialog
        confirmationDialog("Logout", "This is going to erase all your local user data. Are you sure?", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                api.logout().enqueue(new ErrorHandlerCallback(SettingsActivity.this) {
                    @Override
                    public void onResponse(Call<String> call, Response<String> response) {
                        super.onResponse(call, response);

                        // Even if isSuccessful() gives false logout the user locally regardless.

                        // Clear Glide cache
                        AsyncTask.execute(new Runnable() {
                            @Override
                            public void run() {
                                GlideApp.get(SettingsActivity.this).clearDiskCache();
                            }
                        });

                        // Delete email and sessionId preferences
                        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(SettingsActivity.this);
                        preferences.edit().remove("email").remove("sessionId").commit();

                        // Empty the database
                        db.userDao().deleteAllRows();
                        db.messageDao().deleteAllRows();

                        // Open the login screen, clearning the backstack(cannot do that cuz of circular dependencies)
                        // Adhoc solution to circumvent the circulat dependency:
                        Intent intent = null;
                        try {
                            intent = new Intent(SettingsActivity.this, Class.forName("com.group17.login.activities.LoginActivity_"));
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                            finish();
                        } catch (ClassNotFoundException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailure(Call<String> call, Throwable t) {
                        super.onFailure(call, t);
                    }
                });
            }
        });
    }

    // TODO: has the same handler as the logout one. decouple it to reduce repetition
    @Click(R2.id.delete_account_button)
    void deleteUserHandler(View buttonView) {
        // Show "are you sure" dialog
        confirmationDialog("Delete Account", "This is going to erase all your local and remote user data. Are you sure?", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                api.delete().enqueue(new ErrorHandlerCallback(SettingsActivity.this) {
                    @Override
                    public void onResponse(Call<String> call, Response<String> response) {
                        super.onResponse(call, response);

                        // Even if isSuccessful() gives false logout the user locally regardless.

                        // Clear Glide cache
                        AsyncTask.execute(new Runnable() {
                            @Override
                            public void run() {
                                GlideApp.get(SettingsActivity.this).clearDiskCache();
                            }
                        });

                        // Delete email and sessionId preferences
                        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(SettingsActivity.this);
                        preferences.edit().remove("email").remove("sessionId").commit();

                        // Empty the database
                        db.userDao().deleteAllRows();
                        db.messageDao().deleteAllRows();

                        // Open the login screen, clearning the backstack(cannot do that cuz of circular dependencies)
                        // Adhoc solution to circumvent the circulat dependency:
                        Intent intent = null;
                        try {
                            intent = new Intent(SettingsActivity.this, Class.forName("com.group17.login.activities.LoginActivity_"));
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                            finish();
                        } catch (ClassNotFoundException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailure(Call<String> call, Throwable t) {
                        super.onFailure(call, t);
                    }
                });
            }
        });
        // perform delete user request
        // on success:
        // clear the preferences, clear the messages table, clear the user_info table
        // open the login screen, empty the backstack
    }

    @Click(R2.id.confirm_button)
    void confirmButtonHandler(View buttonView) {

        this.showLoadingDialog("Setting user info...");

        final ErrorHandlerCallback matchmakingPictureCallback = new ErrorHandlerCallback(this) {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                super.onResponse(call, response);

                SettingsActivity.this.hideLoadingDialog();

                if(response.isSuccessful()) {
                    finish();
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                super.onFailure(call, t);
                SettingsActivity.this.hideLoadingDialog();
            }
        };

        final ErrorHandlerCallback profilePictureCallback = new ErrorHandlerCallback(this) {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                super.onResponse(call, response);

                if(response.isSuccessful()) {
                    if(SettingsActivity.this.matchPicture != null) {
                        SettingsActivity.this.setMatchPicture(SettingsActivity.this.matchPicture, matchmakingPictureCallback);
                    } else {
                        SettingsActivity.this.hideLoadingDialog();
                        finish();
                    }
                } else {
                    Toast.makeText(context, "bla", Toast.LENGTH_SHORT).show();
                    SettingsActivity.this.hideLoadingDialog();
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                super.onFailure(call, t);
                SettingsActivity.this.hideLoadingDialog();
            }
        };

        api.setUserInfo(
                this.nameInput.getText().toString(),
                this.occupationView.getText().toString(),
                this.birthdayInput.getText().toString(),
                this.interestsInput.getText().toString(),
                this.introductionInput.getText().toString()
        ).enqueue(new ErrorHandlerCallback(SettingsActivity.this) {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                super.onResponse(call, response);

                if(response.isSuccessful()) {
                    if(profilePicture != null) {
                        SettingsActivity.this.setProfilePicture(SettingsActivity.this.profilePicture, profilePictureCallback);
                    } else if(matchPicture != null){
                        SettingsActivity.this.setMatchPicture(SettingsActivity.this.matchPicture, matchmakingPictureCallback);
                    } else {
                        finish();
                    }
                } else {
                    SettingsActivity.this.hideLoadingDialog();
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                super.onFailure(call, t);
                SettingsActivity.this.hideLoadingDialog();
            }
        });
    }

    void setProfilePicture(byte[] image, ErrorHandlerCallback handler) {
        RequestBody imageBody = RequestBody.create(MediaType.parse("application/octet-stream"), image);

        MultipartBody.Part requestBody =
                MultipartBody.Part.createFormData("picture", "profile_picture", imageBody);

        api.setProfilePicture(
                requestBody
        ).enqueue(handler);
    }

    void setMatchPicture(byte[] image, ErrorHandlerCallback handler) {
        RequestBody imageBody = RequestBody.create(MediaType.parse("application/octet-stream"), image);

        MultipartBody.Part requestBody =
                MultipartBody.Part.createFormData("picture", "match_picture", imageBody);

        api.setMatchmakingPicture(
                requestBody
        ).enqueue(handler);
    }

    protected DatePickerDialog datePickerFactory(Calendar calendar) {
        // Fix layout scaling
        DisplayMetrics displayMetrics = this.getResources().getDisplayMetrics();
        Configuration config = this.getResources().getConfiguration();
        config.densityDpi = 213;
        this.getResources().updateConfiguration(config, displayMetrics);

        if(calendar == null) {
            throw new IllegalArgumentException("Error: calendar can't be null");
        }

        DatePickerDialog.OnDateSetListener datePickHandler;
        DatePickerDialog                   datePicker;

        datePickHandler = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                Calendar calendar = Calendar.getInstance();
                calendar.set(year, month, dayOfMonth);
                setDateView(calendar);
            }
        };

        datePicker = new DatePickerDialog(
                this,
                datePickHandler,
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
        );

        datePicker.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
                // Fix layout scaling
                DisplayMetrics displayMetrics = SettingsActivity.this.getResources().getDisplayMetrics();
                Configuration config = SettingsActivity.this.getResources().getConfiguration();

                config.densityDpi = 160;
                SettingsActivity.this.getResources().updateConfiguration(config, displayMetrics);
            }
        });

        datePicker.show();

        return null;
    }

    protected void setDateView(Calendar calendar) {
        if(calendar == null) {
            throw new IllegalArgumentException("Error: calendar can't be null");
        }

        DateFormat format = new SimpleDateFormat("yyyy-MM-dd");

        String output = format.format(calendar.getTime());

        birthdayInput.setText(output);
    }

    @FocusChange(R2.id.birthday_input)
    void birthdayInputFocusHandler(View buttonView) {
        if(buttonView.isFocused()) {
            datePickerFactory(Calendar.getInstance());
        }
    }

    @Click(R2.id.birthday_input)
    void birthdayInputClickHandler(View buttonView) {
        datePickerFactory(Calendar.getInstance());
    }

    public void openHomeActivity() {
//        Intent intent = new Intent(this, HomeActivity_.class);
//        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
//        startActivity(intent);
//        finish();
    }

    @Click(R2.id.menu_button)
    void menuButtonClickHandler(View backButtonView) {
        Toast.makeText(this, "Already in the settings activity.", Toast.LENGTH_SHORT).show();
    }
}
