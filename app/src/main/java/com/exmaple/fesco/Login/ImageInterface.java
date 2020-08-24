package com.exmaple.fesco.Login;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface ImageInterface {

    String RegisterURL = "http://192.168.56.1/fesco/";
    @FormUrlEncoded
    @POST("register.php")
    Call<String> getUser(
            @Field("phone") String phone,
            @Field("address") String address,
            @Field("name") String name,
            @Field("email") String email,
            @Field("password") String password
    );


    String AssToCardURL = "http://192.168.56.1/fesco/";
    @FormUrlEncoded
    @POST("addToShoppingCard.php")
    Call<String> getItem(
            @Field("user_id") String user_id,
            @Field("food_id") String food_id,
            @Field("food_count") String food_count,
            @Field("price") String price,
            @Field("status") String status
    );

    String AddCommentURL = "http://192.168.56.1/fesco/";
    @FormUrlEncoded
    @POST("addComment.php")
    Call<String> getComment(
            @Field("user_id") int user_id,
            @Field("username") String username,
            @Field("title") String title,
            @Field("content") String content,
            @Field("food_id") int food_id
    );

    String removeItemFromShoppingCardURL = "http://192.168.56.1/fesco/";
    @FormUrlEncoded
    @POST("removeFromShoppingCard.php")
    Call<String> removeItemFromShoppingCard(
            @Field("id") int id
    );

}