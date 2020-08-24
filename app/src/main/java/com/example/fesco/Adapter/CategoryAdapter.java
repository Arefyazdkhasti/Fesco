package com.example.fesco.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.fesco.classes.Category;
import com.example.fesco.R;

import java.util.ArrayList;
import java.util.List;

public class CategoryAdapter  extends RecyclerView.Adapter<CategoryAdapter.CatViewHolder> {


    Context context;
    List<Category> categories =new ArrayList<>();
    onCatItemClicked onCatItemClicked;

    public  CategoryAdapter(Context context){
        this.context=context;
    }

    public  CategoryAdapter(Context context,onCatItemClicked onCatItemClicked){
        this.context=context;
        this.onCatItemClicked=onCatItemClicked;
    }


    public void setCategories(List<Category> categories) {
        this.categories = categories;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public CategoryAdapter.CatViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.category_item, parent, false);
        return new CategoryAdapter.CatViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CategoryAdapter.CatViewHolder holder, int position) {
        final Category category=categories.get(position);

        if(category.getId()==4){
            Glide.with(holder.itemView.getContext()).load(R.drawable.pesto_pizza).into(holder.cat_image);
        }else if(category.getId()==5){
            Glide.with(holder.itemView.getContext()).load(R.drawable.navigation_header_image).into(holder.cat_image);
        }else if(category.getId()==6){
            Glide.with(holder.itemView.getContext()).load(R.drawable.drink).into(holder.cat_image);
        }else{
          // extra category !
          //  Glide.with(holder.itemView.getContext()).load(R.drawable.drink).into(holder.cat_image);
        }

        holder.cat_name.setText(category.getName());



        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //send intents
                Toast.makeText(context, category.getName()+" Clicked",Toast.LENGTH_SHORT).show();
                //go to each category class!
                onCatItemClicked.onCatClicked(category);
            }
        });
    }

    @Override
    public int getItemCount() {
        return categories.size();
    }

    public class CatViewHolder extends RecyclerView.ViewHolder{


        ImageView cat_image;
        TextView cat_name;
        onCatItemClicked onCatItemClicked;

        public CatViewHolder(@NonNull View itemView) {
            super(itemView);
            cat_image=itemView.findViewById(R.id.cat_image);
            cat_name=itemView.findViewById(R.id.cat_name);
        }

        public void bindCategory(final Category category){
            cat_name.setText(category.getName());

            if(category.getId()==4){
                Glide.with(itemView.getContext()).load(R.drawable.pesto_pizza).into(cat_image);
            }else if(category.getId()==5){
                Glide.with(itemView.getContext()).load(R.drawable.navigation_header_image).into(cat_image);
            }else if(category.getId()==6){
                Glide.with(itemView.getContext()).load(R.drawable.drink).into(cat_image);
            }

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onCatItemClicked.onCatClicked(category);
                }
            });
        }
    }

    public interface onCatItemClicked{
        void onCatClicked(Category category);
    }
}
