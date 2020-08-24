package com.example.fesco.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.fesco.Adapter.FoodsAdapter;
import com.example.fesco.OpenHelper.DataBaseOpenHelper;
import com.example.fesco.R;
import com.example.fesco.Services.ApiService;
import com.example.fesco.classes.Category;
import com.example.fesco.classes.Food;

import java.util.List;

public class EachCategoryActivity extends AppCompatActivity {

    public static final int PIZZA_CAT_CODE=4;
    public static final int BURGERS_CAT_CODE=5;
    public static final int DRINKS_CAT_CODE=6;

    public static final String EXTRA_KEY_CAT="category";

    RecyclerView recyclerView;
    Category category;

    TextView cat_title;
    ImageView back_btn;

    FoodsAdapter foodsAdapter;
    List<Food> foods;

    FoodsAdapter.onFoodItemClicked onFoodItemClicked=new FoodsAdapter.onFoodItemClicked() {
        @Override
        public void onFoodClick(Food food) {
            FoodDetailsActivity.start(EachCategoryActivity.this, food);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_each_category);

        recyclerView=findViewById(R.id.recycler_view);

        category=getIntent().getParcelableExtra(EXTRA_KEY_CAT);
        if(category==null){
            throw new IllegalStateException("Category is null");
        }

        setupToolbar();
        setupRecyclerView();
    }

    public void setupToolbar(){

        cat_title=findViewById(R.id.category_list_title);
        back_btn=findViewById(R.id.back_arrow);

        cat_title.setText(category.getName());
        back_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    public void setupRecyclerView(){

        switch (category.getId()){
            case PIZZA_CAT_CODE:
                setUpEachCat(PIZZA_CAT_CODE);
                break;
            case BURGERS_CAT_CODE:
                setUpEachCat(BURGERS_CAT_CODE);
                break;
            case DRINKS_CAT_CODE:
                setUpEachCat(DRINKS_CAT_CODE);
                break;
            default:
                Toast.makeText(this, "No such category", Toast.LENGTH_SHORT).show();
                break;

        }
    }

    public void setUpEachCat(int cat_id){
        ApiService apiService = new ApiService(this);

        apiService.getPizzaCategory(new ApiService.onEachCatReceived() {
            @Override
            public void OnReceived(List<Food> foods) {
                EachCategoryActivity.this.foods = foods;
                DataBaseOpenHelper openHelper = new DataBaseOpenHelper(getApplicationContext());
                openHelper.addFoods(foods);
                GridLayoutManager gridLayoutManager = new GridLayoutManager(getApplicationContext(), 2, RecyclerView.VERTICAL, false);
                LinearLayoutManager linearLayoutManager=new LinearLayoutManager(getApplicationContext(),RecyclerView.VERTICAL,true);
                StaggeredGridLayoutManager staggeredGridLayoutManager = new StaggeredGridLayoutManager(2,StaggeredGridLayoutManager.VERTICAL);
                foodsAdapter = new FoodsAdapter(getApplicationContext(),onFoodItemClicked);
                recyclerView.setAdapter(foodsAdapter);
                recyclerView.setLayoutManager(staggeredGridLayoutManager);
                foodsAdapter.setFoods(foods);
            }
        },cat_id);
    }

    public static void start(Context context, Category category){
        Intent intent=new Intent(context, EachCategoryActivity.class);
        intent.putExtra(EXTRA_KEY_CAT, category);
        context.startActivity(intent);
    }
}
