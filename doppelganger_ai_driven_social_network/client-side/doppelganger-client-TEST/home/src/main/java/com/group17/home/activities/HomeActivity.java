package com.group17.home.activities;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.app.Activity;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.group17.home.R;
import com.group17.home.R2;
import com.group17.home.helpers.ConversationsListAdapter;
import com.group17.home.helpers.RecentConversationsModel;
import com.group17.matchmaking.activities.MatchmakingActivity_;
import com.group17.model.SyncService;
import com.group17.model.entities.Message;
import com.group17.model.entities.User;
import com.group17.model.helper.BaseActivity;
import com.group17.model.helper.GlideApp;
import com.group17.model.rest.ErrorHandlerCallback;
import com.group17.settings.activities.SettingsActivity_;

import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Response;

@EActivity
public class HomeActivity extends BaseActivity {

    @ViewById(R2.id.profile_picture_view)
    ImageView profilePicView;

    @ViewById(R2.id.user_name_view)
    TextView userNameView;

    //private RecentConversationsModel model = null;
    private ConversationsListAdapter adapter = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        //this.model = ViewModelProviders.of(this).get(RecentConversationsModel.class);

        // Get the user info of the current user(or try to get it)
        // If the info is unavailable then we need to get it from the cached table
        // if it's not there then logout?

        // pri login(i registration) triabva da polucha i samia user_id ili user_email, da vida koe shte mi potriabva

        ((ListView)HomeActivity.this.findViewById(R.id.recent_conversations_view)).setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(HomeActivity.this.adapter != null) {
                    Message message = HomeActivity.this.adapter.getItem(position);

                    // Get current user email
                    SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(HomeActivity.this);
                    final String email = preferences.getString("email", "");

                    // Try to get the current user id from the database
                    User user = db.userDao().getUserByEmail(email);
                    User partner = null;

                    // If the user is not setup or Home screen wasn't successful in syncing the current user's info yet
                    if(user == null) {
                        // Need to wait for synchronization to happen
                        Toast.makeText(HomeActivity.this, "User synchronization not fully complete.", Toast.LENGTH_SHORT).show();
                    } else {
                        // Determine the correct partner id
                        if(user.user_id == message.senderId) {
                            partner = db.userDao().getUserById(message.partnerId);
                        } else {
                            partner = db.userDao().getUserById(message.senderId);
                        }
                    }
//                    if(user != null) { // Won't be needed when user_info is added when matchmaking happens and constantly synced using the sync service
//                        nameView.setText(user.name);
//                    }

                    if(partner != null) {
                        ConversationActivity_.intent(HomeActivity.this).partnerId(partner.user_id).partnerEmail(partner.email).start();
                    } else {
                        Toast.makeText(HomeActivity.this, "User data not synced or user was deleted.", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        db.userDao().getAllUsers().observe(this, new Observer<List<User>>() {
            @Override
            public void onChanged(@Nullable List<User> messages) {
                // Update UI(neka bude ListView, che niamam vreme za RecyclerView)
                // TODO: will it be called once in the beginning???

                List<Message> conversationsBla = db.messageDao().getStaticConversations();

                // This does the job, but should be fixed on a database query level

                // I need to compare by date too........., removes duplicates
                for(int n = 0; n < conversationsBla.size(); n++) {
                    for(int m = n+1; m < conversationsBla.size(); m++) {
                        if( (conversationsBla.get(n).senderId == conversationsBla.get(m).senderId && conversationsBla.get(n).partnerId == conversationsBla.get(m).partnerId) ||
                                (conversationsBla.get(n).senderId == conversationsBla.get(m).partnerId && conversationsBla.get(n).partnerId == conversationsBla.get(m).senderId)){
                            conversationsBla.remove(m); // remove duplicate and move on
                            continue;
                        }
                    }
                }

                // Sort them by receive_date again, or not needed??? probably not, cuz the're already sorted from the sql query?

                // Not optimal, but should do the job?
                //if(HomeActivity.this.adapter == null) {
                HomeActivity.this.adapter = new ConversationsListAdapter(HomeActivity.this, HomeActivity.this.db, conversationsBla);

                ((ListView)HomeActivity.this.findViewById(R.id.recent_conversations_view)).setAdapter(HomeActivity.this.adapter);
                adapter.notifyDataSetChanged();
//                } else {
//                    adapter.
//                }

            }
        });

        db.messageDao().getConversations().observe(this, new Observer<List<Message>>() {
            @Override
            public void onChanged(@Nullable List<Message> messages) {
                // Update UI(neka bude ListView, che niamam vreme za RecyclerView)
                // TODO: will it be called once in the beginning???

                List<Message> conversationsBla = db.messageDao().getStaticConversations();

                // This does the job, but should be fixed on a database query level

                // I need to compare by date too........., removes duplicates
                for(int n = 0; n < conversationsBla.size(); n++) {
                    for(int m = n+1; m < conversationsBla.size(); m++) {
                        if( (conversationsBla.get(n).senderId == conversationsBla.get(m).senderId && conversationsBla.get(n).partnerId == conversationsBla.get(m).partnerId) ||
                                (conversationsBla.get(n).senderId == conversationsBla.get(m).partnerId && conversationsBla.get(n).partnerId == conversationsBla.get(m).senderId)){
                            conversationsBla.remove(m); // remove duplicate and move on
                            continue;
                        }
                    }
                }

                // Sort them by receive_date again, or not needed??? probably not, cuz the're already sorted from the sql query?

                // Not optimal, but should do the job?
                //if(HomeActivity.this.adapter == null) {
                HomeActivity.this.adapter = new ConversationsListAdapter(HomeActivity.this, HomeActivity.this.db, conversationsBla);

                ((ListView)HomeActivity.this.findViewById(R.id.recent_conversations_view)).setAdapter(HomeActivity.this.adapter);
                adapter.notifyDataSetChanged();
//                } else {
//                    adapter.
//                }

            }
        });



//        int counter = 5;
//        (new Timer()).schedule(new TimerTask() {
//            private int counter = 5;
//            @Override
//            public void run() {
//
//                Message message = new Message();
//                message.senderId = 1;
//                message.partnerId = 2;
//                message.message = "some";
//                message.receiveDate = "RECEIVE_DATE";
//
//                message.partnerId = counter++;
//
//                db.messageDao().insert(message);
//            }
//        }, 0, 1000);
    }

    @Override
    protected void onResume() {
        super.onResume();
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
                        setUserInfoFromCache();
                    }

                    // Insert/update the user info into the local database
                    db.userDao().insert(user);

                    // Set the profile picture image with Glide
                    GlideApp
                            .with(HomeActivity.this)
                            .load("http://206.189.125.23/auth/picture/" + user.profilePicName)
                            .fitCenter()
                            .into(profilePicView);

                    // Set user name
                    userNameView.setText(user.name);

                    hideLoadingDialog();

                } else {
                    // MUST NEVER REACH THIS LINE!!!(but it reaches it anyway for some reason
                    setUserInfoFromCache();
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                super.onFailure(call, t);
                setUserInfoFromCache();
            }

            private void setUserInfoFromCache() {
                // Try to get user_info from the local database using the email
                User user = db.userDao().getUserByEmail(email);

                if(user == null) {
                    // Display the error message and then finish the activity in the handler
                    HomeActivity.this.showTerminationDialog("Insufficient information", "The application hasn't had an opportunity to cache your information and cannot retrieve it from the internet. The application will close now.");
                } else {
                    // Set the profile picture image with Glide
                    GlideApp
                            .with(HomeActivity.this)
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
                HomeActivity.this.finish();
            }
        });
        AlertDialog alert = alertDialog.create();
        alert.show();
    }

    @Click(R2.id.menu_button)
    void menuClickHandler(View buttonView) {
        SettingsActivity_.intent(HomeActivity.this).start();
    }

    @Click(R2.id.matchmaking_button)
    void matchmakingButton(View buttonView) {
        MatchmakingActivity_.intent(HomeActivity.this).start();
    }
}
