package com.group17.model.rest;

import android.content.Context;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

// Subclass to add functionality
public class ErrorHandlerCallback implements Callback<String> {

    protected Context context = null;

    public ErrorHandlerCallback(Context context) {
        this.context = context;
    }

    // These handlers execute in the UI thread: https://futurestud.io/tutorials/retrofit-2-introduction-to-call-adapters
    @Override
    public void onResponse(Call<String> call, Response<String> response) {

        // Check if the response code is not in the range [200, 300)
        if(!response.isSuccessful()) {
            try {
                // Get rationale from JSON response
                JSONObject responseJSONObject = new JSONObject(response.errorBody().string());
                String rationale = responseJSONObject.getString("rationale");

                // Display rationale
                Toast.makeText(context, rationale, Toast.LENGTH_SHORT).show();
            } catch (JSONException e) {
                // MUST NEVER REACH HERE!!! SERVER BUG IN THAT CASE!!!
                e.printStackTrace();
            } catch (IOException e) {
                // TODO: what about this?
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onFailure(Call<String> call, Throwable t) {
        // https://futurestud.io/tutorials/retrofit-2-how-to-detect-network-and-conversion-errors-in-onfailure
        if(t instanceof IOException) {
            // Internet connection issues
            Toast.makeText(context, "Internet connection issues detected.", Toast.LENGTH_SHORT).show();
        } else {
            // MUST NEVER REACH!!!
            t.printStackTrace();
        }
    }
}