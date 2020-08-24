package com.example.fesco.Adapter;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fesco.Activities.MainActivity;
import com.example.fesco.Activities.ShoppingCardActivity;
import com.example.fesco.OpenHelper.ShoppingCardDatabaseOpenHelper;
import com.example.fesco.R;
import com.example.fesco.Services.ApiService;
import com.example.fesco.View.ValueSelector;
import com.example.fesco.classes.Comment;
import com.example.fesco.classes.Food;
import com.example.fesco.classes.ShoppingCard;
import com.exmaple.fesco.Login.ImageInterface;
import com.exmaple.fesco.Login.LoginActivity;
import com.exmaple.fesco.Login.RegisterActivity;
import com.exmaple.fesco.Login.SQLiteHandler;
import com.google.android.material.snackbar.Snackbar;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class ShoppingCardAdapter extends RecyclerView.Adapter<ShoppingCardAdapter.ShoppingCardViewHolder> {


    Context context;
    List<ShoppingCard> shoppingCards = new ArrayList<>();
    private onShoppingCardItemClicked onShoppingCardItemClicked;
    private int ShoppingCardFinalPrice;
    private ShoppingCardDatabaseOpenHelper shoppingCardDatabaseOpenHelper;
    private SQLiteHandler sqLiteHandler;
    String CURRENT_USER;


    public ShoppingCardAdapter(Context context) {
        this.context = context;
        shoppingCardDatabaseOpenHelper = new ShoppingCardDatabaseOpenHelper(context);

        sqLiteHandler = new SQLiteHandler(context);
        HashMap<String, String> user = sqLiteHandler.getUserDetails();
        System.out.println(user);
        CURRENT_USER = user.get("id");

    }

    public ShoppingCardAdapter(Context context, onShoppingCardItemClicked onShoppingCardItemClicked) {
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
    public ShoppingCardViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.shopping_card_item, parent, false);
        return new ShoppingCardViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ShoppingCardViewHolder holder, int position) {
        final ShoppingCard shoppingCard = shoppingCards.get(position);
        String food_name = getFoodName(shoppingCard.getFood_id());
        holder.food_title.setText(food_name);
        holder.food_count.setText(Integer.toString(shoppingCard.getFood_count()));
        holder.final_price.setText(Integer.toString(shoppingCard.getFinal_price()));

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

        holder.delete_item_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Toast.makeText(context, +shoppingCard.getId() + food_name + " " + shoppingCard.getFood_count() + " Clicked", Toast.LENGTH_SHORT).show();
                showAlertDialog(shoppingCard);
            }
        });

        holder.updateItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println("current selected number= " + holder.valueSelector.getValue());
                ApiService apiService = new ApiService(context);
                apiService.updateFoodCountInShoppingCard(CURRENT_USER,
                        Integer.toString(shoppingCard.getFood_id()),
                        Integer.toString(holder.valueSelector.getValue()));
            }
        });
        holder.btn_plus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                incrementValue(holder.valueSelector);
                ApiService apiService = new ApiService(context);
                apiService.updateFoodCountInShoppingCard(CURRENT_USER,
                        Integer.toString(shoppingCard.getFood_id()),
                        Integer.toString(holder.valueSelector.getValue()));

            }
        });

        holder.btn_minus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                decrementValue(holder.valueSelector);
                ApiService apiService = new ApiService(context);
                apiService.updateFoodCountInShoppingCard(CURRENT_USER,
                        Integer.toString(shoppingCard.getFood_id()),
                        Integer.toString(holder.valueSelector.getValue()));

            }
        });


    }

    public void decrementValue(ValueSelector valueSelector){
        int value = valueSelector.getValue();
        valueSelector.setValue(value-1);
    }

    public void incrementValue(ValueSelector valueSelector){
        int value = valueSelector.getValue();
        valueSelector.setValue(value+1);
    }

    public void showAlertDialog(ShoppingCard shoppingCard) {
        AlertDialog alertDialog = new AlertDialog.Builder(context).create();
        alertDialog.setMessage("آیا میخواهید این محصول از سبد خرید شما حذف شود؟");
        alertDialog.setCancelable(true);
        alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "خیر", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                alertDialog.dismiss();
            }
        });
        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "بله", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                deleteItemFromShoppingCard(shoppingCard.getId());
                alertDialog.dismiss();
            }
        });
        alertDialog.show();
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


    public class ShoppingCardViewHolder extends RecyclerView.ViewHolder {

        TextView food_title;
        TextView food_count;
        TextView final_price;
        ValueSelector valueSelector;
        TextView delete_item_btn;
        ImageView btn_plus;
        ImageView btn_minus;
        TextView updateItem;

        public ShoppingCardViewHolder(@NonNull View itemView) {
            super(itemView);
            food_title = itemView.findViewById(R.id.shopping_card_food_title);
            final_price = itemView.findViewById(R.id.final_food_price);
            valueSelector = itemView.findViewById(R.id.count_value_selector);
            delete_item_btn = itemView.findViewById(R.id.delete_item_btn);

            updateItem = itemView.findViewById(R.id.update_shopping_card_item);

            valueSelector.setMaxValue(5);
            valueSelector.setMinValue(0);

            food_count = valueSelector.findViewById(R.id.value_number);
            btn_plus = valueSelector.findViewById(R.id.btn_plus);
            btn_minus = valueSelector.findViewById(R.id.btn_minus);

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

                        Toast.makeText(context, "با موفقیت از سبد خرید شما حذف شد", Toast.LENGTH_LONG).show();
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


