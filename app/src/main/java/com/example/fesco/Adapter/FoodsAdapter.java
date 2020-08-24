package com.example.fesco.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.fesco.classes.Food;
import com.example.fesco.R;

import java.util.ArrayList;
import java.util.List;

public class FoodsAdapter extends RecyclerView.Adapter<FoodsAdapter.FoodsViewHolder> implements Filterable {


    Context context;
    List<Food> foods=new ArrayList<>();
    List<Food> filtered_foods=new ArrayList<>();
    private onFoodItemClicked onFoodItemClicked;

    public  FoodsAdapter(Context context){
        this.context=context;
    }

    public  FoodsAdapter(Context context,onFoodItemClicked onFoodItemClicked){
        this.context=context;
        this.onFoodItemClicked=onFoodItemClicked;
    }

    public void setFoods(List<Food> foods) {
        this.foods = foods;
        this.filtered_foods=foods;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public FoodsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view=LayoutInflater.from(parent.getContext()).inflate(R.layout.foods_item, parent, false);
        return new FoodsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FoodsViewHolder holder, int position) {
        final Food food=filtered_foods.get(position);
        Glide.with(holder.itemView.getContext()).load(food.getFoodImageUrl().replace("localhost", "192.168.56.1")).placeholder(R.drawable.navigation_header_image).into(holder.food_image);
        holder.food_title.setText(food.getTitle());
        holder.food_content.setText(food.getContent());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //send intents
                Toast.makeText(context, food.getTitle()+" Clicked",Toast.LENGTH_SHORT).show();
                onFoodItemClicked.onFoodClick(food);
            }
        });
    }

    @Override
    public int getItemCount() {
        return filtered_foods.size();
    }

    public class FoodsViewHolder extends RecyclerView.ViewHolder{


        ImageView food_image;
        TextView food_title;
        TextView food_content;

        private onFoodItemClicked onFoodItemClicked;

        public FoodsViewHolder(@NonNull View itemView) {
            super(itemView);
            food_image=itemView.findViewById(R.id.foods_image);
            food_title =itemView.findViewById(R.id.food_title);
            food_content=itemView.findViewById(R.id.food_content);
        }

        /*public void bindFood(final Food food){
            Glide.with(itemView.getContext()).load(food.getFoodImageUrl().replace("localhost", "192.168.56.1")).placeholder(R.id.nav_header_imageView).into(food_image);
            food_title.setText(food.getTitle());
            food_content.setText(food.getContent());

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onFoodItemClicked.onFoodClick(food);
                }
            });
        }*/
    }

    public interface onFoodItemClicked{
        void onFoodClick(Food food);
    }


    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                String charString = charSequence.toString();
                if (charString.isEmpty()) {
                    filtered_foods = foods;
                } else {
                    List<Food> filteredList = new ArrayList<>();
                    for (Food row : foods) {

                        // name match condition. this might differ depending on your requirement
                        // here we are looking for name or phone number match
                        if (row.getTitle().toLowerCase().contains(charString.toLowerCase())) {
                            filteredList.add(row);
                        }
                    }

                    filtered_foods = filteredList;
                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = filtered_foods;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                filtered_foods = (ArrayList<Food>) filterResults.values;
                notifyDataSetChanged();
            }
        };
    }

}
