package com.group17.matchmaking.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.group17.matchmaking.R;
import com.group17.matchmaking.R2;
import com.group17.model.entities.User;
import com.group17.model.helper.BaseActivity;
import com.group17.model.helper.GlideApp;
import com.group17.model.rest.ErrorHandlerCallback;

import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import retrofit2.Call;
import retrofit2.Response;

@EActivity
public class MatchmakingActivity extends BaseActivity {

    @ViewById(R2.id.profile_picture_view)
    ImageView profilePictureView;

    @ViewById(R2.id.name_view)
    TextView nameView;

    @ViewById(R2.id.introduction_view)
    TextView introductionView;

    @ViewById(R2.id.birthday_view)
    TextView birthdayView;

    @ViewById(R2.id.occupation_view)
    TextView occupationView;

    @ViewById(R2.id.interests_view)
    TextView interestsView;

    protected int index = 1;
    protected int currentUserId = -1;
    protected String currentUserEmail = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_matchmaking);

        // Try to get the first match. If not match was found then give a Toast and close the activity
        // If a match was not found cuz of the internet connection then do the same stuff

        showLoadingDialog("Loading first match...");

        api.matches(
                index,
                index
        ).enqueue(new ErrorHandlerCallback(MatchmakingActivity.this) {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                super.onResponse(call, response);

                hideLoadingDialog();

                if(response.isSuccessful()) {
                    JSONArray array = null;
                    try {
                        array = new JSONArray(response.body().toString());
                        // If the array is not empty then we have a match!
                        if(array.length() > 0) {
                            displayMatch(array.getJSONObject(0));
                        } else {
                            MatchmakingActivity.this.showError("No match was found");
                            finish();
                        }
                    } catch (JSONException e) {
                        // MUST NEVER BE REACH!!!
                        e.printStackTrace();
                    }
                } else {
                    MatchmakingActivity.this.showError("No match was found");
                    finish();
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                super.onFailure(call, t);
                hideLoadingDialog();
                finish();
            }
        });
    }

    @Click(R2.id.back_button)
    void backButtonHandler(View buttonView) {
        showLoadingDialog("Loading match...");

        if(index <= 1) {
            showError("No more matches before the first match");
            hideLoadingDialog();
            return;
        }

        index = index-1;

        api.matches(
                index,
                index
        ).enqueue(new ErrorHandlerCallback(MatchmakingActivity.this) {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                super.onResponse(call, response);

                hideLoadingDialog();

                if(response.isSuccessful()) {
                    JSONArray array = null;
                    try {
                        array = new JSONArray(response.body().toString());
                        // If the array is not empty then we have a match!
                        if(array.length() > 0) {
                            displayMatch(array.getJSONObject(0));
                        } else {
                            MatchmakingActivity.this.showError("No match was found");
                        }
                    } catch (JSONException e) {
                        // MUST NEVER BE REACH!!!
                        e.printStackTrace();
                    }
                } else {
                    MatchmakingActivity.this.showError("No match was found");
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                super.onFailure(call, t);
                index++;
                hideLoadingDialog();
            }
        });
    }

    // base handler here

    @Click(R2.id.forward_button)
    void forwardButtonHandler(View buttonView) {
        showLoadingDialog("Loading match...");

        index = index+1;

        api.matches(
                index,
                index
        ).enqueue(new ErrorHandlerCallback(MatchmakingActivity.this) {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                super.onResponse(call, response);

                hideLoadingDialog();

                if(response.isSuccessful()) {
                    JSONArray array = null;
                    try {
                        array = new JSONArray(response.body().toString());
                        // If the array is not empty then we have a match!
                        if(array.length() > 0) {
                            displayMatch(array.getJSONObject(0));
                        } else {
                            MatchmakingActivity.this.showError("No more matches were found.");
                            index--;
                        }
                    } catch (JSONException e) {
                        // MUST NEVER BE REACH!!!
                        e.printStackTrace();
                    }
                } else {
                    MatchmakingActivity.this.showError("No more matches were found.");
                    index--;
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                super.onFailure(call, t);
                index--;
                hideLoadingDialog();
            }
        });
    }

    @Click(R2.id.start_conversation_button)
    void startConversationHandler(View buttonView) {
        Intent intent = null;
        try {
            intent = new Intent(MatchmakingActivity.this, Class.forName("com.group17.home.activities.ConversationActivity_")); // TODO: provide the user id!!!
            intent.putExtra("partnerId", currentUserId);
            intent.putExtra("partnerEmail", currentUserEmail);
            startActivity(intent);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    void displayMatch(JSONObject responseJSONObject) {
        // Parse the given user info
        User user = new User();
        try {
            user.user_id = responseJSONObject.getInt("user_id");
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
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG
            ).show();
            // MUST NEVER BE REACHED!!!
            e.printStackTrace();
        }

        // Insert/update the user info into the local database
        db.userDao().insert(user);

        // Set the profile picture image with Glide
        GlideApp
                .with(MatchmakingActivity.this)
                .load("http://206.189.125.23/auth/picture/" + user.profilePicName)
                .fitCenter()
                .into(MatchmakingActivity.this.profilePictureView);

        // Display user information
        nameView.setText(user.name);
        introductionView.setText(user.introduction);
        birthdayView.setText(user.birthday);
        occupationView.setText(user.occupation);
        interestsView.setText(user.interests);
        currentUserId = user.user_id;
        currentUserEmail = user.email;
    }

    @Click(R2.id.menu_button)
    void menuButtonClickHandler(View backButtonView) {
        Toast.makeText(this, "Settings are unavailable from this activity.", Toast.LENGTH_SHORT).show();
    }
}
