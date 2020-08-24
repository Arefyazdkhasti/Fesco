package com.example.fesco.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.bumptech.glide.Glide;
import com.example.fesco.Adapter.CommentAdapter;
import com.example.fesco.Adapter.FoodsAdapter;
import com.example.fesco.OpenHelper.AddToCardDataBaseOpenHelper;
import com.example.fesco.OpenHelper.CommentsDataBaseOpenHelper;
import com.example.fesco.OpenHelper.ShoppingCardDatabaseOpenHelper;
import com.example.fesco.R;
import com.example.fesco.Services.ApiService;
import com.example.fesco.View.ValueSelector;
import com.example.fesco.classes.Comment;
import com.example.fesco.classes.Food;
import com.example.fesco.classes.ShoppingCard;
import com.exmaple.fesco.Login.AppConfig;
import com.exmaple.fesco.Login.AppController;
import com.exmaple.fesco.Login.ImageInterface;
import com.exmaple.fesco.Login.LoginActivity;
import com.exmaple.fesco.Login.RegisterActivity;
import com.exmaple.fesco.Login.SQLiteHandler;
import com.google.android.material.snackbar.Snackbar;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.scalars.ScalarsConverterFactory;


public class FoodDetailsActivity extends AppCompatActivity {

    private static final String TAG = RegisterActivity.class.getSimpleName();
    public static final String EXTRA_KEY_FOOD = "food";

    private ImageView food_image;
    private TextView food_price;
    private TextView food_price_main;
    private TextView food_title;
    private Button add_to_card_btn;
    private Button add_comment_btn;
    private TextView food_content;
    private ScrollView scrollView;
    private ValueSelector valueSelector;

    private EditText title;
    private EditText content;
    private String title_et;
    private String content_et;

    private RecyclerView comments_recyclerView;
    private CommentAdapter commentAdapter;
    private List<Comment> comments;
    private Food food;
    AddToCardDataBaseOpenHelper db;

    SQLiteHandler sqLiteHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food_detail);

        food = getIntent().getParcelableExtra(EXTRA_KEY_FOOD);
        if (food == null) {
            throw new IllegalStateException("food is null");
        }

        sqLiteHandler = new SQLiteHandler(this);
        db = new AddToCardDataBaseOpenHelper(this);
        HashMap<String, String> user = sqLiteHandler.getUserDetails();
        String current_user_id = user.get("id");
        String current_user_name = user.get("name");

        setupView();
        setFoodDetails();

        setupCommentsRecyclerView(food.getId());


        add_to_card_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (valueSelector.getValue() != 0) {
                    Toast.makeText(FoodDetailsActivity.this, "Add to card clicked", Toast.LENGTH_SHORT).show();
                    if (current_user_id != null) {
                  /*  addToCard(Integer.parseInt(current_user_id),
                            food.getId(),
                            valueSelector.getValue(),
                            food.getPrice() * valueSelector.getValue(),
                            0);*/

                        int price = food.getPrice() * valueSelector.getValue();

                        addToCard(current_user_id,
                                Integer.toString(food.getId()),
                                Integer.toString(valueSelector.getValue()),
                                Integer.toString(price),
                                "0");
                    } else {
                        Toast.makeText(FoodDetailsActivity.this, "لطفا اول وارد شوید", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(FoodDetailsActivity.this,LoginActivity.class));
                    }
                } else {
                    Toast.makeText(FoodDetailsActivity.this, "لطفا تعداد را انتخاب کنید", Toast.LENGTH_SHORT).show();
                }

            }
        });


        add_comment_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                title_et = title.getText().toString().trim();
                content_et = content.getText().toString().trim();

                if (!title_et.isEmpty()) {
                    if (!content_et.isEmpty()) {
                        addComment(Integer.parseInt(current_user_id), current_user_name, title_et, content_et, food.getId());
                    } else {
                        Toast.makeText(FoodDetailsActivity.this, "متن نظر را تکمیل کنید", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(FoodDetailsActivity.this, "عنوان نظر را تکمیل کنید", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void setupView() {
        food_image = findViewById(R.id.food_image);
        food_price = findViewById(R.id.food_price_tv);
        food_price_main = findViewById(R.id.food_price);
        food_title = findViewById(R.id.food_title_details);
        add_to_card_btn = findViewById(R.id.add_to_card_btn);
        food_content = findViewById(R.id.food_content);
        valueSelector = findViewById(R.id.food_order_count);
        comments_recyclerView = findViewById(R.id.comments_recycler_view);
        scrollView = findViewById(R.id.scroll_view);
        add_comment_btn = findViewById(R.id.submit_comment_btn);
        title = findViewById(R.id.comment_title_et);
        content = findViewById(R.id.comment_content_et);
    }


    public void setFoodDetails() {

        System.out.println(food.getTitle() + food.getPrice());

        Glide.with(this).load(food.getFoodImageUrl().replace("localhost", "192.168.56.1")).placeholder(R.drawable.navigation_header_image).into(food_image);
        food_price.setText(Integer.toString(food.getPrice()));
        food_price_main.setText(Integer.toString(food.getPrice()));
        food_title.setText(food.getTitle());
        food_content.setText(food.getContent());
    }


    //setup comments with food_id
    public void setupCommentsRecyclerView(int food_id) {
        ApiService apiService = new ApiService(this);

        apiService.getComments(new ApiService.onCommentReceived() {
            @Override
            public void OnReceived(List<Comment> comments) {
                FoodDetailsActivity.this.comments = comments;
                CommentsDataBaseOpenHelper openHelper = new CommentsDataBaseOpenHelper(getApplicationContext());
                openHelper.addComments(comments);
                // GridLayoutManager gridLayoutManager = new GridLayoutManager(getApplicationContext(), 2, RecyclerView.VERTICAL, false);
                LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext(), RecyclerView.HORIZONTAL, true);
                commentAdapter = new CommentAdapter(getApplicationContext());
                comments_recyclerView.setAdapter(commentAdapter);
                comments_recyclerView.setLayoutManager(linearLayoutManager);
                commentAdapter.setFoods(comments);
            }
        }, food_id);
    }

    public static void start(Context context, Food food) {
        Intent intent = new Intent(context, FoodDetailsActivity.class);
        intent.putExtra(EXTRA_KEY_FOOD, food);
        context.startActivity(intent);
    }


    private void addToCard(String user_id, String food_id, String food_count, String price, String status) {

        OkHttpClient okHttpClient = new OkHttpClient().newBuilder()
                .connectTimeout(60, TimeUnit.SECONDS)
                .readTimeout(60, TimeUnit.SECONDS)
                .writeTimeout(60, TimeUnit.SECONDS)
                .build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(ImageInterface.AssToCardURL)
                .addConverterFactory(ScalarsConverterFactory.create())
                .client(okHttpClient)
                .build();

        ImageInterface api = retrofit.create(ImageInterface.class);


        Call<String> call = api.getItem(user_id, food_id, food_count, price, status);

        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, retrofit2.Response<String> response) {
                assert response.body() != null;
                Log.i("Response", response.body());
                System.out.println(response);

                if (response.isSuccessful()) {
                    if (response.body() != null) {
                        Log.i("onSuccess", response.body().toString());

                        // try {
                        //  JSONObject reader = new JSONObject(response.body());
                        //  String error = reader.getString("error");

                        //if (error.equals("false")) {
                        // Printing uploading success message coming from server on android app.

                        // Toast.makeText(RegisterActivity.this, "Registered successfully!", Toast.LENGTH_LONG).show();
                        Snackbar.make(scrollView, "با موقفیت به سبد خرید افزوده شد", Snackbar.LENGTH_INDEFINITE).
                                setAction("سبد خرید", v -> startActivity(new Intent(FoodDetailsActivity.this, ShoppingCardActivity.class))).show();
                           /* } else {
                                String errorMessage = reader.getString("error_msg");
                                // Toast.makeText(RegisterActivity.this, "Error happened! Try again\n"+error_msg , Toast.LENGTH_LONG).show();
                                Snackbar.make(scrollView, "Something went wrong " + errorMessage, Snackbar.LENGTH_INDEFINITE).
                                        setAction("RETRY", v -> {
                                            //retry register here
                                        }).setBackgroundTint(ContextCompat.getColor(FoodDetailsActivity.this, R.color.red)).show();
                                //  }
                            }*/
                      /*  }catch (JSONException e) {
                            e.printStackTrace();
                        }*/
                        // Toast.makeText(RegisterActivity.this, "Registered Successfully!!", Toast.LENGTH_SHORT).show();

                    } else {
                        Log.i("onEmptyResponse", "Returned empty response");
                        Toast.makeText(FoodDetailsActivity.this, "Nothing returned", Toast.LENGTH_LONG).show();

                    }
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                Toast.makeText(FoodDetailsActivity.this, "ERROR: " + t.getMessage(), Toast.LENGTH_LONG).show();
                System.out.println(t.getMessage());
            }
        });
    }


    private void addToCard2(String user_id, String food_id, String food_count, String price, String status) {
        // Tag used to cancel the request
        String tag_string_req = "req_register";


        StringRequest strReq = new StringRequest(Request.Method.POST, AppConfig.URL_ADD_TO_CARD, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d(TAG, "Register Response: " + response.toString());

                try {
                    Log.e("anyText", response);
                    JSONObject jObj = new JSONObject(response);

                    boolean error = jObj.getBoolean("error");
                    if (!error) {
                        JSONObject item = jObj.getJSONObject("user");
                        String user_id = item.getString("user_id");
                        String food_id = item.getString("food_id");
                        String food_count = item.getString("food_count");
                        String price = item.getString("price");
                        String status = item.getString("status");
                        String created_at = item.getString("created_at");


                        // Inserting row in users table
                        db.addItemToCard(user_id, food_id, food_count, price, status, created_at);

                        Toast.makeText(getApplicationContext(), "User successfully registered. Try login now!", Toast.LENGTH_LONG).show();


                        // Launch login activity
                        Intent intent = new Intent(FoodDetailsActivity.this, LoginActivity.class);
                        startActivity(intent);
                        finish();
                    } else {

                        // Error occurred in registration. Get the error
                        // message
                        String errorMsg = jObj.getString("error_msg");
                        Toast.makeText(getApplicationContext(),
                                errorMsg, Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Registration Error: " + error.getMessage());
                Toast.makeText(getApplicationContext(),
                        error.getMessage(), Toast.LENGTH_LONG).show();
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                // Posting params to register url
                Map<String, String> params = new HashMap<String, String>();
                params.put("user_id", user_id);
                params.put("food_id", food_id);
                params.put("food_count", food_count);
                params.put("status", status);

                return params;
            }

        };

        //System.out.println(strReq);
        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
    }


    private void addComment(int user_id, String username, String title, String content, int food_id) {

        OkHttpClient okHttpClient = new OkHttpClient().newBuilder()
                .connectTimeout(60, TimeUnit.SECONDS)
                .readTimeout(60, TimeUnit.SECONDS)
                .writeTimeout(60, TimeUnit.SECONDS)
                .build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(ImageInterface.AddCommentURL)
                .addConverterFactory(ScalarsConverterFactory.create())
                .client(okHttpClient)
                .build();

        ImageInterface api = retrofit.create(ImageInterface.class);


        Call<String> call = api.getComment(user_id, username, title, content, food_id);

        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, retrofit2.Response<String> response) {
                assert response.body() != null;
                Log.i("Response", response.body());
                System.out.println(response);

                if (response.isSuccessful()) {
                    if (response.body() != null) {
                        Log.i("onSuccess", response.body().toString());
                        Snackbar.make(scrollView, "نظر شما ثبت شد", Snackbar.LENGTH_LONG).show();
                    } else {
                        Log.i("onEmptyResponse", "Returned empty response");
                        Toast.makeText(FoodDetailsActivity.this, "Nothing returned", Toast.LENGTH_LONG).show();

                    }
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                Toast.makeText(FoodDetailsActivity.this, "ERROR: " + t.getMessage(), Toast.LENGTH_LONG).show();
                System.out.println(t.getMessage());
            }
        });
    }

}
