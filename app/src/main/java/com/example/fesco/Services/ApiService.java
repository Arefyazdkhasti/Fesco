package com.example.fesco.Services;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.example.fesco.Activities.HistoryActivity;
import com.example.fesco.Activities.MainActivity;
import com.example.fesco.classes.Category;
import com.example.fesco.classes.Comment;
import com.example.fesco.classes.Food;
import com.example.fesco.classes.ShoppingCard;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class ApiService {

    private static final String TAG = "ApiService";
    private Context context;
    private static final String CAT_BASE_URL="http://192.168.56.1/fesco/foods.php";
    private static final String COMMENTS_BASE_URL="http://192.168.56.1/fesco/getComments.php";
    private static final String GET_SHOPPING_CARD_BASE_URL="http://192.168.56.1/fesco/shoppingcard.php";
    private static final String GET_SHOPPING_CARD_TOTAL_PRICE_BASE_URL="http://192.168.56.1/fesco/getCardPrice.php";
    private static final String GET_SHOPPING_CARD_TOTAL_SIZE_BASE_URL="http://192.168.56.1/fesco/getCardSize.php";
    private static final String UPDATE_STATUS_BASE_URL="http://192.168.56.1/fesco/updateStatus.php";
    private static final String UPDATE_FOOD_COUNT_BASE_URL="http://192.168.56.1/fesco/updateShoppingCard.php";


    public ApiService(Context context) {
        this.context = context;
    }

    public void getFoods(final onFoodsReceived onFoodsReceived) {
        final JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, "http://192.168.56.1/fesco/getAllFoods.php", null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                List<Food> foods = new ArrayList<>();
                for (int i = 0; i < response.length(); i++) {
                    Food food = new Food();
                    try {

                        JSONObject jsonObject = response.getJSONObject(i);
                        food.setId(jsonObject.getInt("id"));
                        food.setFoodImageUrl(jsonObject.getString("file_name"));
                        food.setTitle(jsonObject.getString("name"));
                        food.setContent(jsonObject.getString("compounds"));
                        food.setPrice(jsonObject.getInt("price"));
                        food.setSpecial(jsonObject.getInt("special"));
                        food.setCategory_id(jsonObject.getInt("category_id"));
                        food.setOrder_count(jsonObject.getInt("order_count"));
                        foods.add(food);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                onFoodsReceived.OnReceived(foods);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Error in Response: " + error);
            }
        });
        request.setRetryPolicy(new DefaultRetryPolicy(18000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        Volley.newRequestQueue(context).add(request);
    }

    public interface onFoodsReceived {
        void OnReceived(List<Food> foods);
    }


    public void getCategories(final onCategoryReceived onCategoryReceived) {
        final JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, "http://192.168.56.1/fesco/getCategory.php", null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                List<Category> categories = new ArrayList<>();
                for (int i = 0; i < response.length(); i++) {
                    Category category = new Category();
                    try {
                        JSONObject jsonObject = response.getJSONObject(i);
                        category.setId(jsonObject.getInt("id"));
                        category.setName(jsonObject.getString("name"));
                        categories.add(category);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                onCategoryReceived.OnReceived(categories);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Error in Response: " + error);
            }
        });
        request.setRetryPolicy(new DefaultRetryPolicy(18000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        Volley.newRequestQueue(context).add(request);
    }


    public interface onCategoryReceived {
        void OnReceived(List<Category> categories);
    }




    public void getPizzaCategory(final onEachCatReceived onEachCatReceived,int cat_id) {

        final JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, CAT_BASE_URL+"?category="+cat_id, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                List<Food> foods = new ArrayList<>();
                for (int i = 0; i < response.length(); i++) {
                    Food food = new Food();
                    try {

                        JSONObject jsonObject = response.getJSONObject(i);
                        food.setId(jsonObject.getInt("id"));
                        food.setFoodImageUrl(jsonObject.getString("file_name"));
                        food.setTitle(jsonObject.getString("name"));
                        food.setContent(jsonObject.getString("compounds"));
                        food.setPrice(jsonObject.getInt("price"));
                        food.setSpecial(jsonObject.getInt("special"));
                        food.setCategory_id(jsonObject.getInt("category_id"));
                        food.setOrder_count(jsonObject.getInt("order_count"));
                        foods.add(food);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                onEachCatReceived.OnReceived(foods);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Error in Response: " + error);
            }
        });
        request.setRetryPolicy(new DefaultRetryPolicy(18000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        Volley.newRequestQueue(context).add(request);
    }

    public interface onEachCatReceived {
        void OnReceived(List<Food> foods);
    }


    //get Comments from database
    public void getComments(final onCommentReceived onCommentReceived ,int food_id) {

        System.out.println(COMMENTS_BASE_URL+"?food_id="+food_id);
        final JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, COMMENTS_BASE_URL+"?food_id="+food_id, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                List<Comment> comments = new ArrayList<>();
                for (int i = 0; i < response.length(); i++) {
                    Comment comment = new Comment();
                    try {

                        JSONObject jsonObject = response.getJSONObject(i);
                        comment.setId(jsonObject.getInt("id"));
                        comment.setUser_id(jsonObject.getInt("user_id"));
                        comment.setUsername(jsonObject.getString("username"));
                        comment.setTitle(jsonObject.getString("title"));
                        comment.setContent(jsonObject.getString("content"));
                        comment.setDate(jsonObject.getString("date"));
                        comment.setFood_related_id(jsonObject.getInt("food_id"));
                        comments.add(comment);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                System.out.println(comments.size());
                onCommentReceived.OnReceived(comments);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Error in Response: " + error);
            }
        });
        request.setRetryPolicy(new DefaultRetryPolicy(18000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        Volley.newRequestQueue(context).add(request);
    }

    public interface onCommentReceived {
        void OnReceived(List<Comment> comments);
    }


    public void getShoppingCardItem(final onShoppingCardItemReceived onShoppingCardItemReceived ,String user_id,int status) {

        System.out.println(GET_SHOPPING_CARD_BASE_URL+"?userID="+user_id+"&status="+status);
        final JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, GET_SHOPPING_CARD_BASE_URL+"?userID="+user_id+"&status="+status, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                List<ShoppingCard> shoppingCards = new ArrayList<>();
                for (int i = 0; i < response.length(); i++) {
                    ShoppingCard shoppingCard = new ShoppingCard();
                    try {

                        JSONObject jsonObject = response.getJSONObject(i);
                        shoppingCard.setId(jsonObject.getInt("id"));
                        shoppingCard.setUser_id(jsonObject.getInt("user_id"));
                        shoppingCard.setFood_id(jsonObject.getInt("food_id"));
                        shoppingCard.setFood_count(jsonObject.getInt("food_count"));
                        shoppingCard.setFinal_price(jsonObject.getInt("price"));
                        shoppingCard.setStatus(jsonObject.getInt("status"));
                        shoppingCard.setCreated_at(jsonObject.getString("created_at"));
                        shoppingCards.add(shoppingCard);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                System.out.println(shoppingCards.size());
                onShoppingCardItemReceived.OnReceived(shoppingCards);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Error in Response: " + error);
            }
        });
        request.setRetryPolicy(new DefaultRetryPolicy(18000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        Volley.newRequestQueue(context).add(request);
    }

    public interface onShoppingCardItemReceived {
        void OnReceived(List<ShoppingCard> shoppingCards);
    }


    public void getShoppingCardTotalPrice(final onShoppingCardTotalPriceReceived onShoppingCardTotalpriceReceived ,String user_id) {

        System.out.println(GET_SHOPPING_CARD_TOTAL_PRICE_BASE_URL+"?id="+user_id+"$status=0");
        final JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, GET_SHOPPING_CARD_TOTAL_PRICE_BASE_URL+"?id="+user_id+"&status=0", null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {

                int totalPrice_int=0;

                for (int i = 0; i < response.length(); i++) {
                    try {
                        JSONObject jsonObject = response.getJSONObject(i);

                       // System.out.println(jsonObject.getInt("price"));

                        totalPrice_int+=jsonObject.getInt("price");

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                onShoppingCardTotalpriceReceived.OnReceived(totalPrice_int);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Error in Response: " + error);
            }
        });
        request.setRetryPolicy(new DefaultRetryPolicy(18000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        Volley.newRequestQueue(context).add(request);
    }

    public interface onShoppingCardTotalPriceReceived {
        void OnReceived(int ItemPrice);
    }


    public void getShoppingCardTotalSize(final onShoppingCardTotalSizeReceived onShoppingCardTotalSizeReceived ,String user_id) {

        System.out.println(GET_SHOPPING_CARD_TOTAL_SIZE_BASE_URL+"?userID="+user_id);
        final JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, GET_SHOPPING_CARD_TOTAL_SIZE_BASE_URL+"?id="+user_id, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {

                int totalSize=0;

                for (int i = 0; i < response.length(); i++) {
                    try {
                        JSONObject jsonObject = response.getJSONObject(i);

                        // System.out.println(jsonObject.getInt("food_count"));

                        totalSize+=jsonObject.getInt("food_count");

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                onShoppingCardTotalSizeReceived.OnReceived(totalSize);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Error in Response: " + error);
            }
        });
        request.setRetryPolicy(new DefaultRetryPolicy(18000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        Volley.newRequestQueue(context).add(request);
    }

    public interface onShoppingCardTotalSizeReceived {
        void OnReceived(int ItemPrice);
    }



    public void updateStatus(String user_id,View view) {

        final JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, UPDATE_STATUS_BASE_URL+"?id="+user_id, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                Toast.makeText(context, "خرید شما با موفقیت ثبت گردید", Toast.LENGTH_SHORT).show();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Error in Response: " + error);
            }
        });
        request.setRetryPolicy(new DefaultRetryPolicy(18000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        Volley.newRequestQueue(context).add(request);
    }


    public void updateFoodCountInShoppingCard(String user_id,String food_id,String food_count){

        System.out.println(UPDATE_FOOD_COUNT_BASE_URL+"?id="+user_id+"&food_id="+food_id+"&food_count="+food_count);

        final JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, UPDATE_FOOD_COUNT_BASE_URL+"?id="+user_id+"&food_id="+food_id+"&food_count="+food_count, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                Toast.makeText(context, "تغییرات اعمال شد", Toast.LENGTH_SHORT).show();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Error in Response: " + error);
            }
        });
        request.setRetryPolicy(new DefaultRetryPolicy(18000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        Volley.newRequestQueue(context).add(request);
    }
}
