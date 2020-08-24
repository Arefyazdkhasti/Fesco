package com.example.fesco.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.fesco.Adapter.ShoppingCardAdapter;
import com.example.fesco.OpenHelper.ShoppingCardDatabaseOpenHelper;
import com.example.fesco.R;
import com.example.fesco.Services.ApiService;
import com.example.fesco.classes.ShoppingCard;
import com.exmaple.fesco.Login.SQLiteHandler;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;

import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class ShoppingCardActivity extends AppCompatActivity {

    private SwipeRefreshLayout swipeRefreshLayout;
    RecyclerView recyclerView;
    TextView finalCardPrice;
    Button buy_btn;
    ShoppingCardAdapter shoppingCardAdapter;
    List<ShoppingCard> shoppingCards;
    RelativeLayout relativeLayout;
    int totalPrice;
    private String CURRENT_USER;
    static int size;
    SQLiteHandler sqLiteHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shopping_card);
        recyclerView = findViewById(R.id.shopping_card_recycler_view);
        finalCardPrice = findViewById(R.id.final_food_price_shopping_card);
        swipeRefreshLayout = findViewById(R.id.refresh_layout);
        buy_btn = findViewById(R.id.buy_btn);
        relativeLayout=findViewById(R.id.main_layout_shopping_card);

        shoppingCardAdapter = new ShoppingCardAdapter(this);
        sqLiteHandler = new SQLiteHandler(this);
        HashMap<String, String> user = sqLiteHandler.getUserDetails();
        System.out.println(user);
        CURRENT_USER = user.get("id");

        setupToolbar();
        setupShoppingCardRecyclerView(this, 0);
        setupShoppingCardTotalPrice();

        buy_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ApiService apiService=new ApiService(getApplicationContext());
                apiService.updateStatus(CURRENT_USER,relativeLayout);
                Snackbar.make(relativeLayout, "خرید شما با موفقیت ثبت گردید", BaseTransientBottomBar.LENGTH_INDEFINITE).setAction("تاریخچه خرید", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        startActivity(new Intent(ShoppingCardActivity.this, HistoryActivity.class));
                    }
                }).show();
                setupShoppingCardRecyclerView(getApplicationContext(), 0);
                setupShoppingCardTotalPrice();
            }
        });


        swipeRefreshLayout.setOnRefreshListener(() -> new

                Handler().

                postDelayed(() ->

                {
                    shoppingCardAdapter.clear();
                    setupShoppingCardRecyclerView(this, 0);
                    setupShoppingCardTotalPrice();
                    swipeRefreshLayout.setRefreshing(false);
                }, 1000));
        swipeRefreshLayout.setColorSchemeColors(ContextCompat.getColor(this, R.color.light_orange), ContextCompat.getColor(this, R.color.light_blue));

    }

    public void setupToolbar() {

        Toolbar toolbar = findViewById(R.id.shopping_card_toolbar);
        setSupportActionBar(toolbar);

        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowCustomEnabled(true);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    public void setupShoppingCardRecyclerView(final Context context, int status) {
        ApiService apiService = new ApiService(this);

        apiService.getShoppingCardItem(new ApiService.onShoppingCardItemReceived() {
            @Override
            public void OnReceived(List<ShoppingCard> shoppingCards) {
                ShoppingCardActivity.this.shoppingCards = shoppingCards;
                ShoppingCardDatabaseOpenHelper openHelper = new ShoppingCardDatabaseOpenHelper(context);
                openHelper.addShoppingCardItems(shoppingCards);
                // GridLayoutManager gridLayoutManager = new GridLayoutManager(context, 2, RecyclerView.VERTICAL, false);
                LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context, RecyclerView.VERTICAL, false);
                shoppingCardAdapter = new ShoppingCardAdapter(context);
                recyclerView.setAdapter(shoppingCardAdapter);
                recyclerView.setLayoutManager(linearLayoutManager);
                shoppingCardAdapter.setShoppingCards(shoppingCards);
            }
        }, CURRENT_USER, status);

    }

    public void setupShoppingCardTotalPrice() {
        ApiService apiService = new ApiService(this);

        apiService.getShoppingCardTotalPrice(new ApiService.onShoppingCardTotalPriceReceived() {
            @Override
            public void OnReceived(int price) {
                ShoppingCardActivity.this.totalPrice = price;
                finalCardPrice.setText(Integer.toString(totalPrice));
            }
        }, CURRENT_USER);

    }


    @Override
    protected void onResume() {
        super.onResume();
        setupShoppingCardTotalPrice();
        setupShoppingCardRecyclerView(this, 0);
    }
}
