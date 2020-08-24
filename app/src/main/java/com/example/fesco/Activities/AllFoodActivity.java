package com.example.fesco.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.app.SearchManager;
import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.example.fesco.Adapter.FoodsAdapter;
import com.example.fesco.OpenHelper.DataBaseOpenHelper;
import com.example.fesco.R;
import com.example.fesco.Services.ApiService;
import com.example.fesco.classes.Food;

import java.util.List;

public class AllFoodActivity extends AppCompatActivity {

    List<Food> foods;
    FoodsAdapter foodsAdapter;
    RecyclerView allFoodsRecyclerView;
    private SearchView searchView;


    FoodsAdapter.onFoodItemClicked onFoodItemClicked=new FoodsAdapter.onFoodItemClicked() {
        @Override
        public void onFoodClick(Food food) {
            FoodDetailsActivity.start(AllFoodActivity.this, food);
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_food);
        allFoodsRecyclerView=findViewById(R.id.all_foods_recycler_view);

        whiteNotificationBar(allFoodsRecyclerView);

        setupToolbar();
        setupAllFoodsRecyclerView(this);
    }

    public void setupToolbar(){

    Toolbar toolbar=findViewById(R.id.all_food_toolbar);
    setSupportActionBar(toolbar);

    getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    getSupportActionBar().setDisplayShowCustomEnabled(true);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            finish();
        }
    });
}

    private void setupAllFoodsRecyclerView(final Context context){
        ApiService apiService = new ApiService(this);

        apiService.getFoods(new ApiService.onFoodsReceived() {
            @Override
            public void OnReceived(List<Food> foods) {
                AllFoodActivity.this.foods = foods;
                DataBaseOpenHelper openHelper = new DataBaseOpenHelper(context);
                openHelper.addFoods(foods);
                GridLayoutManager gridLayoutManager = new GridLayoutManager(context, 2, RecyclerView.VERTICAL, false);
                LinearLayoutManager linearLayoutManager=new LinearLayoutManager(context,RecyclerView.VERTICAL,true);
                StaggeredGridLayoutManager staggeredGridLayoutManager = new StaggeredGridLayoutManager(2,StaggeredGridLayoutManager.VERTICAL);
                foodsAdapter = new FoodsAdapter(context,onFoodItemClicked);
                allFoodsRecyclerView.setAdapter(foodsAdapter);
                allFoodsRecyclerView.setLayoutManager(staggeredGridLayoutManager);
                foodsAdapter.setFoods(foods);
            }
        });
    }

    public List<Food> getAllFoods(){
        return foods;
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);

        // Associate searchable configuration with the SearchView
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        searchView = (SearchView) menu.findItem(R.id.action_search)
                .getActionView();
        searchView.setSearchableInfo(searchManager
                .getSearchableInfo(getComponentName()));
        searchView.setMaxWidth(Integer.MAX_VALUE);

        // listening to search query text change
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                // filter recycler view when query submitted
                foodsAdapter.getFilter().filter(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String query) {
                // filter recycler view when text is changed
                foodsAdapter.getFilter().filter(query);
                return false;
            }
        });
        return true;
    }

    @Override
    public void onBackPressed() {
        // close search view on back button pressed
        if (!searchView.isIconified()) {
            searchView.setIconified(true);
            return;
        }
        super.onBackPressed();
    }

    private void whiteNotificationBar(View view) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            int flags = view.getSystemUiVisibility();
            flags |= View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;
            view.setSystemUiVisibility(flags);
            getWindow().setStatusBarColor(Color.WHITE);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_search) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
