package com.group17.model.db.migrations;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.RoomDatabase;

import com.group17.model.db.dao.MessageDao;
import com.group17.model.db.dao.UserDao;
import com.group17.model.entities.Message;
import com.group17.model.entities.User;

@Database(entities = {Message.class, User.class}, version = 1)
public abstract class DoppelgangerDatabase extends RoomDatabase {
    public abstract MessageDao messageDao();
    public abstract UserDao userDao();
}
