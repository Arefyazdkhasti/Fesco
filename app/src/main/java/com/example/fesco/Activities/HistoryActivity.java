package com.example.fesco.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.fesco.Adapter.HistoryAdapter;
import com.example.fesco.Adapter.ShoppingCardAdapter;
import com.example.fesco.OpenHelper.ShoppingCardDatabaseOpenHelper;
import com.example.fesco.R;
import com.example.fesco.Services.ApiService;
import com.example.fesco.classes.ShoppingCard;
import com.exmaple.fesco.Login.SQLiteHandler;

import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class HistoryActivity extends AppCompatActivity {

    private SwipeRefreshLayout swipeRefreshLayout;
    RecyclerView recyclerView;
    HistoryAdapter historyAdapter;
    List<ShoppingCard> history;
    private String CURRENT_USER;
    SQLiteHandler sqLiteHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        recyclerView = findViewById(R.id.history_recycler_view);
        swipeRefreshLayout = findViewById(R.id.refresh_layout);

        historyAdapter = new HistoryAdapter(this);
        sqLiteHandler = new SQLiteHandler(this);
        HashMap<String, String> user = sqLiteHandler.getUserDetails();
        System.out.println(user);
        CURRENT_USER = user.get("id");

        setupToolbar();
        setupShoppingCardRecyclerView(this,1);


        swipeRefreshLayout.setOnRefreshListener(() -> new Handler().postDelayed(() -> {
            historyAdapter.clear();
            setupShoppingCardRecyclerView(this,1);
            swipeRefreshLayout.setRefreshing(false);
        }, 1000));
        swipeRefreshLayout.setColorSchemeColors(ContextCompat.getColor(this, R.color.light_orange), ContextCompat.getColor(this, R.color.light_blue));


    }

    public void setupToolbar() {

        Toolbar toolbar = findViewById(R.id.history_toolbar);
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

    private void setupShoppingCardRecyclerView(final Context context, int status) {
        ApiService apiService = new ApiService(this);

        apiService.getShoppingCardItem(new ApiService.onShoppingCardItemReceived() {
            @Override
            public void OnReceived(List<ShoppingCard> shoppingCards) {
                HistoryActivity.this.history = shoppingCards;
                ShoppingCardDatabaseOpenHelper openHelper = new ShoppingCardDatabaseOpenHelper(context);
                openHelper.addShoppingCardItems(shoppingCards);
                // GridLayoutManager gridLayoutManager = new GridLayoutManager(context, 2, RecyclerView.VERTICAL, false);
                LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context, RecyclerView.VERTICAL, false);
                historyAdapter = new HistoryAdapter(context);
                recyclerView.setAdapter(historyAdapter);
                recyclerView.setLayoutManager(linearLayoutManager);
                historyAdapter.setShoppingCards(shoppingCards);
            }
        }, CURRENT_USER,status);

    }

}
