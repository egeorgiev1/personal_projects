package com.group17.model.db.dao;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import com.group17.model.entities.Message;

import java.util.List;

@Dao
public interface MessageDao {

    @Query("SELECT * FROM (SELECT * FROM Message GROUP BY partner_id ORDER BY MAX(strftime('%Y-%m-%d %H:%i:%S', receive_date))) ORDER BY receive_date DESC")
    LiveData<List<Message>> getConversations(); // List of most recent messages for every conversations, sort by most recently active conversation

    @Query("SELECT * FROM (SELECT * FROM Message GROUP BY partner_id ORDER BY MAX(strftime('%Y-%m-%d %H:%i:%S', receive_date))) ORDER BY receive_date DESC")
    List<Message> getStaticConversations();

    // Mai ne mi triabva order-by zasega po sa???
    @Query("SELECT * FROM Message WHERE sender_id=:userId OR partner_id=:userId")
    LiveData<List<Message>> getMessagesForConversation(int userId);

    @Query("SELECT * FROM Message ORDER BY receive_date DESC LIMIT 1")
    Message getLastMessage(); // For the Messaging service

    @Insert
    void insert(Message message);

    @Query("DELETE FROM Message")
    void deleteAllRows();
}