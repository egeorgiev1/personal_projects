package com.group17.model.helper;

import android.app.Application;
import android.arch.persistence.db.SupportSQLiteDatabase;
import android.arch.persistence.room.BuildConfig;
import android.arch.persistence.room.Room;
import android.content.Context;

import com.group17.model.db.migrations.DoppelgangerDatabase;
import com.group17.model.rest.IDoppelgangerAPI;
import com.group17.model.rest.SessionIdCapturer;
import com.group17.model.rest.SessionIdInjector;

import java.lang.reflect.Method;
import java.util.HashMap;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class DoppelgangerApplication extends Application {

    private static DoppelgangerApplication app;

    public IDoppelgangerAPI api = null;
    public DoppelgangerDatabase db = null;

    public static DoppelgangerApplication getApp(){
        return app;
    }

    public DoppelgangerApplication() {
        super.onCreate();
        app = this;
    }

    // DEBUG
    public static void setInMemoryRoomDatabases(SupportSQLiteDatabase... database) {
        if (BuildConfig.DEBUG) {
            try {
                Class<?> debugDB = Class.forName("com.amitshekhar.DebugDB");
                Class[] argTypes = new Class[]{HashMap.class};
                HashMap<String, SupportSQLiteDatabase> inMemoryDatabases = new HashMap<>();
                // set your inMemory databases
                inMemoryDatabases.put("InMemoryOne.db", database[0]);
                Method setRoomInMemoryDatabase = debugDB.getMethod("setInMemoryRoomDatabases", argTypes);
                setRoomInMemoryDatabase.invoke(null, inMemoryDatabases);
            } catch (Exception ignore) {

            }
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();

        // Create Retrofit client
        OkHttpClient.Builder clientBuilder = new OkHttpClient.Builder();
        clientBuilder.addInterceptor(new SessionIdCapturer(this));
        clientBuilder.addInterceptor(new SessionIdInjector(this));
        OkHttpClient client = clientBuilder.build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://206.189.125.23/auth/")
                .addConverterFactory(ScalarsConverterFactory.create())
                .client(client)
                .build();

        this.api = retrofit.create(IDoppelgangerAPI.class);

        // Create the database
        db = Room.databaseBuilder(
                this,
                DoppelgangerDatabase.class,
                "doppelganger_db"
        ).allowMainThreadQueries().build(); // Not recommended to do it in the main thread for performance

        // DEBUG
        setInMemoryRoomDatabases(db.getOpenHelper().getReadableDatabase());
    }
}
