package com.group17.model.entities;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

@Entity
public class User {
    @PrimaryKey
    public int user_id;

    @ColumnInfo(name = "email")
    public String email;

    @ColumnInfo(name = "name")
    public String name;

    @ColumnInfo(name = "occupation")
    public String occupation;

    @ColumnInfo(name = "birthday")
    public String birthday;

    @ColumnInfo(name = "interests")
    public String interests;

    @ColumnInfo(name = "introduction")
    public String introduction;

    @ColumnInfo(name = "profile_pic_name")
    public String profilePicName;

    @ColumnInfo(name = "match_pic_name")
    public String matchPicName;

    @ColumnInfo(name = "face_id")
    public String faceId;
}