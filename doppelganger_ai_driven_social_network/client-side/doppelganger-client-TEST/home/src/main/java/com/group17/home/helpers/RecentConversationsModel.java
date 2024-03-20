package com.group17.home.helpers;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModel;

import com.group17.model.db.migrations.DoppelgangerDatabase;
import com.group17.model.entities.Message;

import java.util.List;

public class RecentConversationsModel extends ViewModel {
    // get the LiveData object from the database here: https://developer.android.com/topic/libraries/architecture/viewmodels
    // https://blog.iamsuleiman.com/android-architecture-components-tutorial-room-livedata-viewmodel/
    private DoppelgangerDatabase db;

    public RecentConversationsModel(DoppelgangerDatabase db) {
        this.db = db;
    }

    public LiveData<List<Message>> getConversations() {
        return db.messageDao().getConversations();
    }
}
