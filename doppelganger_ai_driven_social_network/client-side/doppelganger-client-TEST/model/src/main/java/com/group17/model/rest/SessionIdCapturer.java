package com.group17.model.rest;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Response;

// TODO: test!!!

/**
 * Only one cookie is set by the server-side and this is the session id
 */
public class SessionIdCapturer implements Interceptor {

    private Context context;

    /**
     * Acquire the context for the usage of shared preferences
     */
    public SessionIdCapturer(Context context) {
        this.context = context;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        Response response = chain.proceed(chain.request());
        if (!response.headers("Set-Cookie").isEmpty()) {
            String sessionId = response.headers("Set-Cookie").get(0);

            // Update the shared preferences with the new session id cookie
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
            preferences.edit().putString("sessionId", sessionId).commit();
        }
        return response;
    }
}
