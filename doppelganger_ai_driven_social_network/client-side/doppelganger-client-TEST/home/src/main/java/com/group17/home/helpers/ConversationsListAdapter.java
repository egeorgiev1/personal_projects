package com.group17.home.helpers;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.group17.home.R;
import com.group17.home.activities.ConversationActivity;
import com.group17.home.activities.HomeActivity;
import com.group17.model.db.migrations.DoppelgangerDatabase;
import com.group17.model.entities.Message;
import com.group17.model.entities.User;
import com.group17.model.helper.GlideApp;

import java.util.List;

public class ConversationsListAdapter extends ArrayAdapter<Message> {

    private List<Message> conversations;
    private Context context = null;
    private DoppelgangerDatabase db = null;
    public ConversationsListAdapter(Context context, DoppelgangerDatabase db, List<Message> conversations) {
        super(context,0, conversations);
        this.conversations = conversations;
        this.context = context;
        this.db = db;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        // Inflate conversation view
        View conversationView = inflater.inflate(R.layout.conversation_layout, parent, false);

        TextView nameView = (TextView) conversationView.findViewById(R.id.name_view);
        TextView lastMessageView = (TextView) conversationView.findViewById(R.id.last_message_view);
        ImageView profilePictureView = (ImageView) conversationView.findViewById(R.id.profile_picture_view);

        Message message = conversations.get(position);

        // Get current user email
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        final String email = preferences.getString("email", "");

        // Try to get the current user id from the database
        User user = db.userDao().getUserByEmail(email);
        User partner = null;

        // If the user is not setup or Home screen wasn't successful in syncing the current user's info yet
        if(user == null) {
            // Need to wait for synchronization to happen
            Toast.makeText(context, "User synchronization not fully complete.", Toast.LENGTH_SHORT).show();
        } else {
            // Determine the correct partner id
            if(user.user_id == message.senderId) {
                partner = db.userDao().getUserById(message.partnerId);
            } else {
                partner = db.userDao().getUserById(message.senderId);
            }
        }

        if(partner != null) { // Needed when you login from a clean machine and the user_info was not synced!!!
            nameView.setText(partner.name);
            // Set the profile picture image with Glide
            GlideApp
                    .with(context)
                    .load("http://206.189.125.23/auth/picture/" + partner.profilePicName)
                    .fitCenter()
                    .into(profilePictureView);
        }
        lastMessageView.setText(message.message);

//        nameView.setText("Test Test Test Test Test Test Test Test Test Test Test Test Test Test Test Test Test Test Test Test Test ");
//        lastMessageView.setText("Test Test Test Test Test Test Test Test Test Test Test Test Test Test Test Test Test Test Test Test Test ");

        return conversationView;
    }

    public Message getItem(int position){
        return this.conversations.get(position);
    }
}
