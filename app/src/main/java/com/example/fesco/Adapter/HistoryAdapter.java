package com.example.fesco.Adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fesco.Activities.MainActivity;
import com.example.fesco.OpenHelper.ShoppingCardDatabaseOpenHelper;
import com.example.fesco.R;
import com.example.fesco.View.ValueSelector;
import com.example.fesco.classes.Comment;
import com.example.fesco.classes.Food;
import com.example.fesco.classes.ShoppingCard;
import com.exmaple.fesco.Login.ImageInterface;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.HistoryCardViewHolder> {


    Context context;
    List<ShoppingCard> shoppingCards = new ArrayList<>();
    private ShoppingCardAdapter.onShoppingCardItemClicked onShoppingCardItemClicked;
    private int ShoppingCardFinalPrice;
    private ShoppingCardDatabaseOpenHelper shoppingCardDatabaseOpenHelper;


    public HistoryAdapter(Context context) {
        this.context = context;
        shoppingCardDatabaseOpenHelper = new ShoppingCardDatabaseOpenHelper(context);
    }

    public HistoryAdapter(Context context, ShoppingCardAdapter.onShoppingCardItemClicked onShoppingCardItemClicked) {
        this.context = context;
        this.onShoppingCardItemClicked = onShoppingCardItemClicked;
    }

    public void setShoppingCards(List<ShoppingCard> shoppingCards) {
        this.shoppingCards = shoppingCards;
        notifyDataSetChanged();
    }


    public void clear() {
        this.shoppingCards = new ArrayList<>();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public HistoryCardViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.buying_history_item, parent, false);
        return new HistoryCardViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HistoryCardViewHolder holder, int position) {
        final ShoppingCard shoppingCard = shoppingCards.get(position);
        String food_name = getFoodName(shoppingCard.getFood_id());
        holder.food_title.setText(food_name);
        holder.food_count.setText(Integer.toString(shoppingCard.getFood_count()));
        holder.final_price.setText(Integer.toString(shoppingCard.getFinal_price()));
        holder.date.setText(shoppingCard.getCreated_at());

        ShoppingCardFinalPrice += shoppingCard.getFinal_price();

        //System.out.println(ShoppingCardFinalPrice);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //send intents
                Toast.makeText(context, food_name + " " + shoppingCard.getFood_count() + " Clicked", Toast.LENGTH_SHORT).show();
                //     onShoppingCardItemClicked.onShoppingCardClick(comment);
            }
        });

       /* holder.delete_item_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Toast.makeText(context, +shoppingCard.getId() + food_name + " " + shoppingCard.getFood_count() + " Clicked", Toast.LENGTH_SHORT).show();
                deleteItemFromShoppingCard(shoppingCard.getId());
            }
        });*/
    }

    public String getFoodName(int id) {
        List<Food> foods = MainActivity.getAllFoods();
        for (int i = 0; i < foods.size(); i++) {
            if (foods.get(i).getId() == id) {
                return foods.get(i).getTitle();
            }
        }
        return null;
    }

    @Override
    public int getItemCount() {
        return shoppingCards.size();
    }

    public int getShoppingCardFinalPrice() {
        return ShoppingCardFinalPrice;
    }


    public class HistoryCardViewHolder extends RecyclerView.ViewHolder {

        TextView food_title;
        TextView food_count;
        TextView final_price;
        TextView date;
        // TextView delete_item_btn;

        public HistoryCardViewHolder(@NonNull View itemView) {
            super(itemView);
            food_title = itemView.findViewById(R.id.history_food_title);
            final_price = itemView.findViewById(R.id.history_food_price);
            food_count = itemView.findViewById(R.id.history_food_count);
            date = itemView.findViewById(R.id.date);
//            delete_item_btn = itemView.findViewById(R.id.delete_item_btn);
        }
    }

    public interface onShoppingCardItemClicked {
        void onShoppingCardClick(Comment comment);
    }

    private void deleteItemFromShoppingCard(int id) {
        OkHttpClient okHttpClient = new OkHttpClient().newBuilder()
                .connectTimeout(60, TimeUnit.SECONDS)
                .readTimeout(60, TimeUnit.SECONDS)
                .writeTimeout(60, TimeUnit.SECONDS)
                .build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(ImageInterface.removeItemFromShoppingCardURL)
                .addConverterFactory(ScalarsConverterFactory.create())
                .client(okHttpClient)
                .build();

        ImageInterface api = retrofit.create(ImageInterface.class);


        Call<String> call = api.removeItemFromShoppingCard(id);

        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, retrofit2.Response<String> response) {
                assert response.body() != null;
                Log.i("Response", response.body());
                System.out.println(response);
                //Toast.makeText()
                // Inserting row in users table

                if (response.isSuccessful()) {
                    if (response.body() != null) {
                        Log.i("onSuccess", response.body().toString());

                        Toast.makeText(context, "با موفقییت از سبد خرید شما حذف شد", Toast.LENGTH_LONG).show();
                     /*   Snackbar.make(scrollView, "Registered successfully.You can Login now", Snackbar.LENGTH_INDEFINITE).
                                setAction("Login", v -> context.startActivity(new Intent(context, LoginActivity.class))).show();*/

                    } else {
                        Log.i("onEmptyResponse", "Returned empty response");
                        Toast.makeText(context, "Nothing returned", Toast.LENGTH_LONG).show();

                    }
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                Toast.makeText(context, "ERROR: " + t.getMessage(), Toast.LENGTH_LONG).show();
                System.out.println(t.getMessage());
            }
        });
    }
}


