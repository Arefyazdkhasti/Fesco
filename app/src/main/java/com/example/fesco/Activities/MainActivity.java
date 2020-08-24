package com.example.fesco.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.fesco.Adapter.CategoryAdapter;
import com.example.fesco.Adapter.FoodsAdapter;
import com.example.fesco.OpenHelper.DataBaseOpenHelper;
import com.example.fesco.Services.ApiService;
import com.example.fesco.classes.Category;
import com.example.fesco.classes.Food;
import com.example.fesco.R;
import com.exmaple.fesco.Login.LoginActivity;
import com.exmaple.fesco.Login.SQLiteHandler;
import com.exmaple.fesco.Login.SessionManager;
import com.google.android.material.navigation.NavigationView;

import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import ss.com.bannerslider.ImageLoadingService;
import ss.com.bannerslider.Slider;
import ss.com.bannerslider.adapters.SliderAdapter;
import ss.com.bannerslider.viewholder.ImageSlideViewHolder;

public class MainActivity extends AppCompatActivity {

    static List<Food> foods;
    List<Category> categories;
    FoodsAdapter foodsAdapter;
    CategoryAdapter categoryAdapter;
    RecyclerView foodsRecyclerView;
    RecyclerView catsRecyclerView;
    ImageView card_btn;
    TextView toolbar_basket_count;
    TextView card_count;
    int totalSize;

    private String CURRENT_USER;

    TextView seeAllBtn;
    private Slider bannerSlider;
    SessionManager sessionManager;
    SQLiteHandler db;


    FoodsAdapter.onFoodItemClicked onFoodItemClicked = new FoodsAdapter.onFoodItemClicked() {
        @Override
        public void onFoodClick(Food food) {
            FoodDetailsActivity.start(MainActivity.this, food);
        }
    };

    CategoryAdapter.onCatItemClicked onCatItemClicked = new CategoryAdapter.onCatItemClicked() {
        @Override
        public void onCatClicked(Category category) {
            EachCategoryActivity.start(MainActivity.this, category);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        foodsRecyclerView = findViewById(R.id.all_foods_recycler_view);
        catsRecyclerView = findViewById(R.id.category_recycler_view);
        seeAllBtn = findViewById(R.id.see_all_btn);
        card_btn = findViewById(R.id.cart);
        toolbar_basket_count = findViewById(R.id.toolbar_basket_count);
        card_count = findViewById(R.id.toolbar_basket_count);


        sessionManager = new SessionManager(this);
        db = new SQLiteHandler(getApplicationContext());


        HashMap<String, String> user = db.getUserDetails();
        System.out.println(user);
        CURRENT_USER = user.get("id");

        setupToolbar();
        setUpNavigationView();
        setupFoodsRecyclerView(this);
        setupCatRecyclerView(this);
        setupShoppingCardTotalPrice();

        seeAllBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, AllFoodActivity.class));
            }
        });

        card_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, ShoppingCardActivity.class));
            }
        });
        Slider.init(new PicassoImageLoadingService(this));
        bannerSlider = findViewById(R.id.banner_slider);
        bannerSlider.setAdapter(new MainSliderAdapter());

        System.out.println("size= " + ShoppingCardActivity.size);
    }


    private void setupFoodsRecyclerView(final Context context) {
        ApiService apiService = new ApiService(this);

        apiService.getFoods(new ApiService.onFoodsReceived() {
            @Override
            public void OnReceived(List<Food> foods) {
                MainActivity.this.foods = foods;
                DataBaseOpenHelper openHelper = new DataBaseOpenHelper(context);
                openHelper.addFoods(foods);
                GridLayoutManager gridLayoutManager = new GridLayoutManager(context, 1, RecyclerView.HORIZONTAL, true);
                LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context, RecyclerView.HORIZONTAL, true);
                foodsAdapter = new FoodsAdapter(context, onFoodItemClicked);
                foodsRecyclerView.setAdapter(foodsAdapter);
                foodsRecyclerView.setLayoutManager(linearLayoutManager);
                foodsAdapter.setFoods(foods);
            }
        });
    }

    private void setupCatRecyclerView(final Context context) {
        ApiService apiService = new ApiService(this);

        apiService.getCategories(new ApiService.onCategoryReceived() {
            @Override
            public void OnReceived(List<Category> categories) {
                MainActivity.this.categories = categories;
                DataBaseOpenHelper openHelper = new DataBaseOpenHelper(context);
                openHelper.addCats(categories);
                GridLayoutManager gridLayoutManager = new GridLayoutManager(context, 2, RecyclerView.VERTICAL, false);
                LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context, RecyclerView.HORIZONTAL, true);
                categoryAdapter = new CategoryAdapter(context, onCatItemClicked);
                catsRecyclerView.setAdapter(categoryAdapter);
                catsRecyclerView.setLayoutManager(linearLayoutManager);
                categoryAdapter.setCategories(categories);
            }
        });
    }

    private void setupToolbar() {

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitleTextColor(ContextCompat.getColor(this, R.color.white));
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayShowTitleEnabled(false);


        DrawerLayout drawerLayout = findViewById(R.id.drawer_layout);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);

        ActionBarDrawerToggle drawerToggle = new ActionBarDrawerToggle(this
                , drawerLayout, toolbar, 0, 0);

        //برای مورفیلینگ انیمیشن
        drawerLayout.addDrawerListener(drawerToggle);
        drawerToggle.syncState();

    }

    private void setUpNavigationView() {
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.navigation_menu_home:
                        startActivity(new Intent(MainActivity.this, MainActivity.class));
                        break;
                    case R.id.navigation_menu_profile:
                        startActivity(new Intent(MainActivity.this, ProfileActivity.class));
                        break;
                    case R.id.navigation_menu_card:
                        startActivity(new Intent(MainActivity.this, ShoppingCardActivity.class));
                        break;
                    case R.id.navigation_menu_history:
                        startActivity(new Intent(MainActivity.this, HistoryActivity.class));
                        break;
                    case R.id.logout:
                        logoutUser();
                        break;
                }
                return true;
            }
        });
    }


    public class MainSliderAdapter extends SliderAdapter {

        @Override
        public int getItemCount() {
            return 3;
        }

        @Override
        public void onBindImageSlide(int position, ImageSlideViewHolder viewHolder) {
            switch (position) {
                case 0:
                    viewHolder.bindImageSlide(R.drawable.pic1);
                    break;
                case 1:
                    viewHolder.bindImageSlide((R.drawable.pic2));
                    break;
                case 2:
                    viewHolder.bindImageSlide(R.drawable.pic3);
                    break;
            }
        }
    }

    public class PicassoImageLoadingService implements ImageLoadingService {
        public Context context;

        public PicassoImageLoadingService(Context context) {
            this.context = context;
        }

        @Override
        public void loadImage(String url, ImageView imageView) {
            Glide.with(context).load(url).into(imageView);
        }

        @Override
        public void loadImage(int resource, ImageView imageView) {
            Glide.with(context).load(resource).into(imageView);
        }

        @Override
        public void loadImage(String url, int placeHolder, int errorDrawable, ImageView imageView) {
            Glide.with(context).load(url).placeholder(placeHolder).error(errorDrawable).into(imageView);
        }
    }

    public static List<Food> getAllFoods() {
        return foods;
    }


    private void logoutUser() {
        sessionManager.setLogin(false);

        db.deleteUsers();

        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
    }

    private void setupShoppingCardTotalPrice() {
        ApiService apiService = new ApiService(this);

        apiService.getShoppingCardTotalSize(new ApiService.onShoppingCardTotalSizeReceived() {
            @Override
            public void OnReceived(int ItemPrice) {
                MainActivity.this.totalSize = ItemPrice;
                if (totalSize != 0) {
                    card_count.setText(Integer.toString(totalSize));
                } else {
                    card_count.setVisibility(View.INVISIBLE);
                }
            }
        }, CURRENT_USER);
    }

    @Override
    protected void onResume() {
        super.onResume();
        setupShoppingCardTotalPrice();
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        setupShoppingCardTotalPrice();
    }

}
