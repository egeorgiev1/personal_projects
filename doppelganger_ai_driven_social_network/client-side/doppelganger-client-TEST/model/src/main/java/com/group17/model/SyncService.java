package com.group17.model;

import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.util.Log;
import android.widget.Toast;

import com.group17.model.db.migrations.DoppelgangerDatabase;
import com.group17.model.entities.Message;
import com.group17.model.entities.User;
import com.group17.model.helper.DoppelgangerApplication;
import com.group17.model.rest.ErrorHandlerCallback;
import com.group17.model.rest.IDoppelgangerAPI;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SyncService extends Service {

    public IDoppelgangerAPI api = null;
    public DoppelgangerDatabase db = null;

    public SyncService() {
        // Get Retrofit client
        this.api = DoppelgangerApplication.getApp().api;

        // Get the database
        this.db = DoppelgangerApplication.getApp().db;
    }

    private String[] names = new String[5]; // TODO: make it an array list??? dinamichno da se promenia goleminata

    private String lastTimestamp = null;

    private Handler messagesSyncHandler = null;
    private Handler usersInfoSyncHandler = null;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        final Message lastMessage = db.messageDao().getLastMessage();
        if(lastMessage == null) {
            lastTimestamp = "1998-01-23 00:00:00";
        } else {
            lastTimestamp = lastMessage.receiveDate;
        }

        // Sync messages
        messagesSyncHandler = new Handler();

        Runnable repeatingTask = new Runnable() {
            @Override
            public void run() {
                // That's for handling the situation when you logout to reset the lastTimestamp
                final Message lastMessage = db.messageDao().getLastMessage();
                if(lastMessage == null) {
                    lastTimestamp = "1998-01-23 00:00:00";
                }

                // Get the next 5 messages
                api.getMessages(lastTimestamp, 5).enqueue(new Callback<String>() { // TODO: tova shte e dosadno da dava errors postoianno dokato niama internet, taka che da ne go izpolzvam errorhandlercallbacka, a standartnia si callback???
                    @Override
                    public void onResponse(Call<String> call, Response<String> response) {
                        if(response.isSuccessful()) {
                            Log.d("Response", response.body().toString());

                            // Parse response
                            JSONArray array = null;
                            try {
                                array = new JSONArray(response.body().toString());
                                // If the array is not empty then we have a match!
                                if(array.length() > 0) {
                                    for(int n = 0; n < array.length(); n++) {
                                        addMessage(array.getJSONObject(n));
                                    }
                                }
                            } catch (JSONException e) {
                                // MUST NEVER BE REACH!!!
                                e.printStackTrace();
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<String> call, Throwable t) {
                    }
                });;

                messagesSyncHandler.postDelayed(this, 500); // TODO: make it 500ms
            }
        };

        messagesSyncHandler.post(repeatingTask);

        // Sync user_info
        usersInfoSyncHandler = new Handler();

        Runnable syncUserInfo = new Runnable() {

            // kogato prevarti currentPartnerIndex togava zapochvame ot nachalo!!!
            private int currentPartnerIndex;
            private List<Message> conversations; // da vzema samia

            // TODO: triabva da moga po id da mu vzema infoto sushto, da napravia nov server script???
            @Override
            public void run() {
                // Get current user email
                SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(SyncService.this);
                final String email = preferences.getString("email", "");

                // If the user is not logged in
                if(email.length() == 0) {
                    usersInfoSyncHandler.postDelayed(this, 2000);
                    return;
                }

                // Try to get the current user id from the database
                User user = db.userDao().getUserByEmail(email);

                // If the user is not setup or Home screen wasn't successful in syncing the current user's info yet
                if(user == null) {
                    usersInfoSyncHandler.postDelayed(this, 2000);
                    return;
                }

                conversations = db.messageDao().getStaticConversations();
                for(Message message: conversations) {
                    int partnerId = -1;
                    if(user.user_id == message.senderId) {
                        partnerId = message.partnerId;
                    } else {
                        partnerId = message.senderId;
                    }

                    // For the current partnerId sync the data in the database
                    api.getUserInfoById(partnerId).enqueue(new Callback<String>() { // TODO: tova shte e dosadno da dava errors postoianno dokato niama internet, taka che da ne go izpolzvam errorhandlercallbacka, a standartnia si callback???
                        @Override
                        public void onResponse(Call<String> call, Response<String> response) {
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

                                // Get the old user info
                                User oldUser = db.userDao().getUserById(user.user_id);

                                // Compare to prevent the Home Screen from constantly updating

                                // If no info was found
                                if(oldUser == null) {
                                    // Insert/update the user info into the local database
                                    db.userDao().insert(user);
                                } else if( // Check if there's any difference between the data
                                        user.user_id != oldUser.user_id ||
                                        !user.email.equals(oldUser.email) ||
                                        !user.name.equals(oldUser.name) ||
                                        !user.occupation.equals(oldUser.occupation) ||
                                        !user.birthday.equals(oldUser.birthday) ||
                                        !user.interests.equals(oldUser.interests) ||
                                        !user.introduction.equals(oldUser.introduction) ||
                                        !user.profilePicName.equals(oldUser.profilePicName) ||
                                        !user.matchPicName.equals(oldUser.matchPicName) ||
                                        !user.faceId.equals(oldUser.faceId)
                                ) {
                                    db.userDao().insert(user);
                                }

                            }
                        }

                        @Override
                        public void onFailure(Call<String> call, Throwable t) {
                        }
                    });;

                }

                usersInfoSyncHandler.postDelayed(this, 2000); // TODO: make it 500ms
            }
        };

        usersInfoSyncHandler.post(syncUserInfo);

        return Service.START_STICKY;
    }

    void addMessage(JSONObject responseJSONObject) {
        // Parse the message
        Message message = new Message();
        try {
            message.senderId = responseJSONObject.getInt("sender_id");
            message.partnerId = responseJSONObject.getInt("receiver_id");
            message.message = responseJSONObject.getString("message");
            message.receiveDate = responseJSONObject.getString("receive_date");
        } catch (JSONException e) {
            // MUST NEVER BE REACHED!!!
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }

        // Insert/update in the database
        db.messageDao().insert(message);

        lastTimestamp = message.receiveDate;

    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        messagesSyncHandler.removeCallbacksAndMessages(null);
        usersInfoSyncHandler.removeCallbacksAndMessages(null);
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
