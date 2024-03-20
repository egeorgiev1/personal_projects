package com.group17.login.presenters;

import android.arch.persistence.db.SupportSQLiteDatabase;
import android.arch.persistence.room.BuildConfig;
import android.arch.persistence.room.Room;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.widget.Toast;

import com.group17.login.activities.LoginActivity;
import com.group17.login.interfaces.ILoginPresenter;
import com.group17.login.interfaces.ILoginView;
import com.group17.model.db.migrations.DoppelgangerDatabase;
import com.group17.model.entities.User;
import com.group17.model.rest.ErrorHandlerCallback;
import com.group17.model.rest.IDoppelgangerAPI;
import com.group17.model.rest.SessionIdCapturer;
import com.group17.model.rest.SessionIdInjector;
import com.group17.setup.activities.SetupActivity;

import org.androidannotations.annotations.EBean;
import org.json.JSONException;
import org.json.JSONObject;
import org.xml.sax.ErrorHandler;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class LoginPresenter implements ILoginPresenter {

    private Context context;
    private ILoginView loginView;
    private IDoppelgangerAPI api = null;
    private DoppelgangerDatabase db = null;

    final ScheduledThreadPoolExecutor executor = new ScheduledThreadPoolExecutor(1);

    public LoginPresenter() {
    }

    public void login(String email, String password) {
        email = email.toLowerCase();

        loginView.showLoadingDialog("Signing In...");

        // Get user info handler
        final ErrorHandlerCallback userInfoHandler = new ErrorHandlerCallback(context) {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                super.onResponse(call, response);

                if(response.isSuccessful()) {
                    // Parse the given user info
                    JSONObject responseJSONObject = null;
                    try {
                        User user = new User();
                        responseJSONObject = new JSONObject(response.body().toString());

                        // TODO: That may not be needed, cuz of the code in the Home screen!!!
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

                        // Insert the user info into the local database
                        db.userDao().insert(user); // TODO: what if some stuff is null???

                        // If the user is not fully setup, open the Setup Activity(destroying the backstack)
                        if(user.profilePicName == null || user.profilePicName.length() == 0 ||
                                user.matchPicName == null || user.matchPicName.length() == 0) {
                            LoginPresenter.this.loginView.openSetupActivity(); // TODO: there's no way he's not fully setup with how currently the system works so that won't be reached
                        } else { // Otherwise open the Home Activity(destroying the backstack)
                            LoginPresenter.this.loginView.openHomeActivity();
                        }
                    } catch (JSONException e) {
                        // MUST NEVER BE REACHED!!!

                        // Delete email and sessionId preferences
                        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
                        preferences.edit().remove("email").remove("sessionId").commit();
                        e.printStackTrace();
                        return;
                    }
                }

                // If there's no user info then he's not setup and we need to open the Setup Activity(destroying the backstack)
                if(!response.isSuccessful()) {
                    LoginPresenter.this.loginView.openSetupActivity();
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                super.onFailure(call, t);
                // Delete email and sessionId preferences
                SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
                preferences.edit().remove("email").remove("sessionId").commit();
                LoginPresenter.this.loginView.hideLoadingDialog();
            }
        };

        // Add email to the preferences
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        preferences.edit().putString("email", email.toLowerCase()).commit();

        api.login(
                email,
                password
        ).enqueue(new ErrorHandlerCallback(context) {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                super.onResponse(call, response);

                if(response.isSuccessful()) {
                    // Fetch the user info of the logged-in user
                    api.getUserInfo("").enqueue(userInfoHandler);
                } else {
                    LoginPresenter.this.loginView.hideLoadingDialog();
                }

            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                super.onFailure(call, t);
                LoginPresenter.this.loginView.hideLoadingDialog();
            }
        });
    }

    public void attach(ILoginView loginView) {
        this.context = (Context) loginView;
        this.loginView = loginView;

        // Create Retrofit client
        OkHttpClient.Builder clientBuilder = new OkHttpClient.Builder();
        clientBuilder.addInterceptor(new SessionIdCapturer(context));
        clientBuilder.addInterceptor(new SessionIdInjector(context));
        OkHttpClient client = clientBuilder.build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://206.189.125.23/auth/")
                .addConverterFactory(ScalarsConverterFactory.create())
                .client(client)
                .build();

        this.api = retrofit.create(IDoppelgangerAPI.class);

        // Create the database
        db = Room.databaseBuilder(
                context.getApplicationContext(),
                DoppelgangerDatabase.class,
                "doppelganger_db"
        ).allowMainThreadQueries().build(); // Not recommended to do it in the main thread for performance

        // DEBUG
        setInMemoryRoomDatabases(db.getOpenHelper().getReadableDatabase());
    }

    // cancel all http requests here, remove all view references here
    public void detach() {
        loginView = null;
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
}
