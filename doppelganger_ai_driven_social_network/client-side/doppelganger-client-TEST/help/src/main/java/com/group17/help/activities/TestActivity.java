package com.group17.help.activities;

import android.arch.persistence.db.SupportSQLiteDatabase;
import android.arch.persistence.room.BuildConfig;
import android.arch.persistence.room.Room;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Pair;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.group17.help.R;
import com.group17.help.R2;
import com.group17.help.helpers.ImagePickerNormalizationHandler;
import com.group17.model.db.migrations.DoppelgangerDatabase;
import com.group17.model.helper.GlideApp;
import com.group17.model.rest.ErrorHandlerCallback;
import com.group17.model.rest.IDoppelgangerAPI;
import com.group17.model.rest.SessionIdCapturer;
import com.group17.model.rest.SessionIdInjector;

import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.HashMap;

import me.zhanghai.android.materialprogressbar.IndeterminateHorizontalProgressDrawable;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import pl.aprilapps.easyphotopicker.DefaultCallback;
import pl.aprilapps.easyphotopicker.EasyImage;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.scalars.ScalarsConverterFactory;

@EActivity
public class TestActivity extends AppCompatActivity {

    IDoppelgangerAPI api = null;
    DoppelgangerDatabase db = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);

        // Create Retrofit client
        OkHttpClient.Builder clientBuilder = new OkHttpClient.Builder();
        clientBuilder.addInterceptor(new SessionIdCapturer(TestActivity.this));
        clientBuilder.addInterceptor(new SessionIdInjector(TestActivity.this));
        OkHttpClient client = clientBuilder.build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://206.189.125.23/auth/")
                .addConverterFactory(ScalarsConverterFactory.create())
                .client(client)
                .build();

        this.api = retrofit.create(IDoppelgangerAPI.class);

        // Create the database
        db = Room.databaseBuilder(
                getApplicationContext(),
                DoppelgangerDatabase.class,
                "doppelganger_db"
        ).build();

        // DEBUG
        setInMemoryRoomDatabases(db.getOpenHelper().getReadableDatabase());
    }

    // DEBUG
    public static void setInMemoryRoomDatabases(SupportSQLiteDatabase... database) {
        if (BuildConfig.DEBUG) {
            try {
                Class<?> debugDB = Class.forName("com.amitshekhar.DebugDB");
                Class[] argTypes = new Class[]{HashMap.class};
                HashMap<String, SupportSQLiteDatabase> inMemoryDatabases = new HashMap<>();
                // set your inMemory databases
                inMemoryDatabases.put("InMemoryOne.db", database[0]);
                Method setRoomInMemoryDatabase = debugDB.getMethod("setInMemoryRoomDatabases", argTypes);
                setRoomInMemoryDatabase.invoke(null, inMemoryDatabases);
            } catch (Exception ignore) {

            }
        }
    }

    // Login
    @ViewById(R2.id.login_email)
    EditText loginEmail;

    @ViewById(R2.id.login_password)
    EditText loginPassword;

    @Click(R2.id.login_button)
    void loginClickHandler(View buttonView) {
        api.login(
            loginEmail.getText().toString(),
            loginPassword.getText().toString()
        ).enqueue(new ErrorHandlerCallback(TestActivity.this)); // TODO: Open setup screen in the handler
    }

    // Register
    @ViewById(R2.id.register_email)
    EditText registerEmail;

    @ViewById(R2.id.register_password)
    EditText registerPassword;

    @ViewById(R2.id.register_confirm_password)
    EditText registerConfirmPassword;

    @Click(R2.id.register_button)
    void registerClickHandler(View buttonView) {
        api.register(
            registerEmail.getText().toString(),
            registerPassword.getText().toString(),
            registerConfirmPassword.getText().toString()
        ).enqueue(new ErrorHandlerCallback(TestActivity.this)); // TODO: Open setup screen in the handler
    }

    // Logout
    @Click(R2.id.logout_button)
    void logoutClickHandler(View buttonView) {
        // TODO: unset cookie in the handler, open the login activity by emptying the backstack, empty all cache, databases
        api.logout().enqueue(new ErrorHandlerCallback(TestActivity.this));
    }

    // Delete User
    @Click(R2.id.delete_user_button)
    void deleteUserClickHandler(View buttonView) {
        // TODO: unset cookie in the handler, open the login activity by emptying the backstack, empty all cache, databases(deleting triggers logout handling)
        api.delete().enqueue(new ErrorHandlerCallback(TestActivity.this));
    }

    // Set User Info
    @ViewById(R2.id.user_name)
    EditText name;

    @ViewById(R2.id.user_occupation)
    EditText occupation;

    @ViewById(R2.id.user_birthday)
    EditText birthday;

    @ViewById(R2.id.user_interests)
    EditText interests;

    @ViewById(R2.id.user_introduction)
    EditText introduction;

    @Click(R2.id.set_user_info_button)
    void setUserInfoClickHandler(View buttonView) {
        // TODO: Add user info to the database in the handler
        api.setUserInfo(
                name.getText().toString(),
                occupation.getText().toString(),
                birthday.getText().toString(),
                interests.getText().toString(),
                introduction.getText().toString()
        ).enqueue(new ErrorHandlerCallback(TestActivity.this) {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                super.onResponse(call, response);
                // to get the user info I need the email of the current user. I don't have it....... neither it is in the localdb
                // TODO: Refetch the user-info and add/update in the local database on success
            }
        });
    }

    private DefaultCallback imagePickerNormalizationHandler = null;

    // Set Profile Picture
    @Click(R2.id.set_profile_picture_button)
    void setProfilePictureClickHandler(View buttonView) {
        // Set handler for a profile picture
        imagePickerNormalizationHandler = new ImagePickerNormalizationHandler(TestActivity.this) {
            @Override
            public void onImagePicked(File imageFile, EasyImage.ImageSource source, int type) {
                super.onImagePicked(imageFile, source, type);
                setProfilePicture(outputImage);
            }
        };
        EasyImage.openCamera(TestActivity.this, 0);
    }

    // Set Matchmaking Picture
    @Click(R2.id.set_matchmaking_picture_button)
    void setMatchmakingPictureClickHandler(View buttonView) {
        // Set handler for a matchmaking picture
        imagePickerNormalizationHandler = new ImagePickerNormalizationHandler(TestActivity.this) {
            @Override
            public void onImagePicked(File imageFile, EasyImage.ImageSource source, int type) {
                super.onImagePicked(imageFile, source, type);
                setMatchmakingPicture(outputImage);
            }
        };
        EasyImage.openCamera(TestActivity.this, 0);
    }

    // Handle photo taking result
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        EasyImage.handleActivityResult(requestCode, resultCode, data, this, imagePickerNormalizationHandler);
    }

    private void setProfilePicture(byte[] image) {
        RequestBody imageBody = RequestBody.create(MediaType.parse("application/octet-stream"), image);

        MultipartBody.Part requestBody =
                MultipartBody.Part.createFormData("picture", "profile_picture", imageBody);

        // TODO: In the handler refetch the user-info and update in the local database on success
        api.setProfilePicture(
                requestBody
        ).enqueue(new ErrorHandlerCallback(TestActivity.this));
    }

    private void setMatchmakingPicture(byte[] image) {
        RequestBody imageBody = RequestBody.create(MediaType.parse("application/octet-stream"), image);

        MultipartBody.Part requestBody =
                MultipartBody.Part.createFormData("picture", "profile_picture", imageBody);

        // TODO: In the handler refetch the user-info and update in the local database on success
        api.setMatchmakingPicture(
                requestBody
        ).enqueue(new ErrorHandlerCallback(TestActivity.this));
    }

    // Get User Info
    @ViewById(R2.id.user_info_view)
    TextView userInfo;

    @ViewById(R2.id.user_info_email)
    EditText userInfoEmail;

    @ViewById(R2.id.profile_picture_view)
    ImageView profilePictureView;

    // TODO: TEST!!!
    @Click(R2.id.get_user_info_button)
    void getUserInfoClickHandler(View buttonView) {
        api.getUserInfo(
                userInfoEmail.getText().toString()
        ).enqueue(new ErrorHandlerCallback(TestActivity.this) {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                super.onResponse(call, response);
                if(response.isSuccessful()) {
                    userInfo.setText(response.body().toString());
                    // CHECK IF PROFILE PICTURE IS SET BEFORE DOING THIS!!!, DO IT WITH CACHING!!!, testvane bez internet??? disable server ili ot samia emulator???
                    JSONObject responseJSONObject = null;
                    try {
                        responseJSONObject = new JSONObject(response.body().toString());
                        String picName = responseJSONObject.getString("profile_pic_name");
                        Toast.makeText(TestActivity.this, picName, Toast.LENGTH_SHORT).show();
                        GlideApp
                                .with(TestActivity.this)
                                .load("http://10.0.2.2/auth/picture/"+picName)
                                // TODO: da gi podkaram tezi loading raboti i placeholders
//                                .placeholder(new IndeterminateHorizontalProgressDrawable(TestActivity.this))
//                                .fallback(new IndeterminateHorizontalProgressDrawable(TestActivity.this))
//                                .error(new IndeterminateHorizontalProgressDrawable(TestActivity.this))
                                .fitCenter()
                                .into(profilePictureView);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    // Get Matches
    @ViewById(R2.id.matches_view)
    TextView matches;

    // TODO: TEST!!!
    @Click(R2.id.get_matches_button)
    void getMatchesClickHandler(View buttonView) {
        api.matches(
                1,
                3
        ).enqueue(new ErrorHandlerCallback(TestActivity.this) {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                super.onResponse(call, response);
                if(response.isSuccessful()) {
                    matches.setText(response.body().toString());
                } else {
                    try {
                        Toast.makeText(context, response.errorBody().string(), Toast.LENGTH_LONG).show();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }
}
