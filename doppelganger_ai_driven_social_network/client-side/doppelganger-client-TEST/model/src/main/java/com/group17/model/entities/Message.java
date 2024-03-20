package com.group17.model.entities;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

@Entity
public class Message {
    @PrimaryKey(autoGenerate = true)
    public int id;

    @ColumnInfo(name = "sender_id")
    public int senderId;

    @ColumnInfo(name = "partner_id")
    public int partnerId;

    @ColumnInfo(name = "message")
    public String message;

    @ColumnInfo(name = "receive_date")
    public String receiveDate;
}