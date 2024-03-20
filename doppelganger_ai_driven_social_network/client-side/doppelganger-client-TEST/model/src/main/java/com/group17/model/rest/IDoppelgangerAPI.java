package com.group17.model.rest;

import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Query;

public interface IDoppelgangerAPI {

    // User Authentication and Management
    @FormUrlEncoded
    @POST("login.php")
    Call<String> login(@Field("email") String email, @Field("password") String password);

    @FormUrlEncoded
    @POST("register.php")
    Call<String> register(@Field("email") String email, @Field("password") String password, @Field("confirm_password") String confirm_password);

    @POST("logout.php")
    Call<String> logout();

    @POST("delete-user.php")
    Call<String> delete(); // TODO: TEST!!!

    // User Setup
    @FormUrlEncoded
    @POST("set-user-info.php")
    Call<String> setUserInfo(@Field("name") String name,
                           @Field("occupation") String occupation,
                           @Field("birthday") String birthday,
                           @Field("interests") String interests,
                           @Field("introduction") String introduction);

    @Multipart
    @POST("set-profile-picture.php")
    Call<String> setProfilePicture(@Part MultipartBody.Part profilePicture);

    @Multipart
    @POST("set-matchmaking-picture.php")
    Call<String> setMatchmakingPicture(@Part MultipartBody.Part matchmakingPicture); // TODO: TEST!!!

    // Rewrite to work with user_id???
    // If the email field is an empty string then the user-info of the current user is going to be returned
    @FormUrlEncoded
    @POST("user-info.php")
    Call<String> getUserInfo(@Field("email") String email); // TODO: TEST!!!

    // Matchmaking
    @GET("matches.php")
    Call<String> matches(@Query("from") int from, @Query("to") int to); // TODO: TEST!!!

    // Messaging
    @FormUrlEncoded
    @POST("send-message.php")
    Call<String> sendMessage(@Field("user_id") int userId, @Field("message") String message); // TODO: TEST!!!

    // NOTE: Timestamp could be a date and modify the Retrofit configuration to do the right conversion??? zasega neka bude string
    @FormUrlEncoded
    @POST("get-messages.php")
    Call<String> getMessages(@Field("timestamp") String timestamp, @Field("number_of_messages") int numberOfMessages); // TODO: TEST!!!

    @FormUrlEncoded
    @POST("get-user-info-by-id.php")
    Call<String> getUserInfoById(@Field("id") int id); // TODO: TEST!!!
}
