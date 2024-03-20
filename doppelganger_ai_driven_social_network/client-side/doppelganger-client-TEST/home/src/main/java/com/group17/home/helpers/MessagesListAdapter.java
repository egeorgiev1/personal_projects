package com.group17.home.helpers;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.group17.home.R;
import com.group17.model.db.migrations.DoppelgangerDatabase;
import com.group17.model.entities.Message;

import java.util.List;

import javax.annotation.ParametersAreNonnullByDefault;

public class MessagesListAdapter extends ArrayAdapter<Message> {

    private List<Message> messages;
    private Context context = null;
    private DoppelgangerDatabase db = null;
    private int partnerId = -1;
    public MessagesListAdapter(Context context, DoppelgangerDatabase db, List<Message> messages, int partnerId) {
        super(context,0, messages);
        this.messages = messages;
        this.context = context;
        this.db = db;
        this.partnerId = partnerId;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        // Inflate conversation view
        View conversationView = inflater.inflate(R.layout.message_layout, parent, false);

        LinearLayout beforeSpace = (LinearLayout) conversationView.findViewById(R.id.before_space);
        LinearLayout afterSpace = (LinearLayout) conversationView.findViewById(R.id.after_space);
        TextView messageView = (TextView) conversationView.findViewById(R.id.message_view);
        TextView dateView = (TextView) conversationView.findViewById(R.id.date_view);
        LinearLayout messageBackground = (LinearLayout) conversationView.findViewById(R.id.message_background);

        Message message = messages.get(position);

        if(message.senderId != partnerId) {
            beforeSpace.setVisibility(View.VISIBLE);
            afterSpace.setVisibility(View.GONE);
            messageView.setTextColor(Color.parseColor("#fad7e3"));
            dateView.setTextColor(Color.parseColor("#f5b0c7"));
        } else {
            beforeSpace.setVisibility(View.GONE);
            afterSpace.setVisibility(View.VISIBLE);
            messageView.setTextColor(Color.parseColor("#2c252e"));
            dateView.setTextColor(Color.parseColor("#705f73"));
            messageBackground.setBackgroundColor(Color.parseColor("#e1bee7"));
        }

//        TextView nameView = (TextView) conversationView.findViewById(R.id.name_view);
//        TextView lastMessageView = (TextView) conversationView.findViewById(R.id.last_message_view);
//        ImageView profilePictureView = (ImageView) conversationView.findViewById(R.id.profile_picture_view);

        messageView.setText(message.message);
        dateView.setText(message.receiveDate);

        return conversationView;
    }

    public Message getItem(int position){
        return this.messages.get(position);
    }
}
