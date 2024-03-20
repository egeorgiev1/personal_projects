package com.group17.model.rest;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

// TODO: test!!!

/**
 * Only one cookie is set by the server-side and this is the session id
 */
public class SessionIdInjector implements Interceptor {

    private Context context;

    /**
     * Acquire the context for the usage of shared preferences
     */
    public SessionIdInjector(Context context) {
        this.context = context;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        // Get the session id from the shared preferences
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        String sessionId = preferences.getString("sessionId", "");

        // Add session id as a cookie to the request
        Request.Builder builder = chain.request().newBuilder();;
        if(sessionId.length() != 0) {
            builder.addHeader("Cookie", sessionId);
        }

        return chain.proceed(builder.build());
    }
}
