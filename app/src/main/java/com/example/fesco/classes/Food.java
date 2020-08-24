package com.example.fesco.classes;

import android.os.Parcel;
import android.os.Parcelable;

public class Food implements Parcelable {
    private int id;
    private String title;
    private int price;
    private String content;
    private int special=0;
    private int category_id;
    private int order_count=0;
    private String foodImageUrl;


    public String getFoodImageUrl() {
        return foodImageUrl;
    }

    public void setFoodImageUrl(String foodImageUrl) {
        this.foodImageUrl = foodImageUrl;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public int getOrder_count() {
        return order_count;
    }

    public void setOrder_count(int order_count) {
        this.order_count = order_count;
    }

    public int getSpecial() {
        return special;
    }

    public void setSpecial(int special) {
        this.special = special;
    }

    public int getCategory_id() {
        return category_id;
    }

    public void setCategory_id(int category_id) {
        this.category_id = category_id;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.id);
        dest.writeString(this.title);
        dest.writeInt(this.price);
        dest.writeString(this.content);
        dest.writeInt(this.special);
        dest.writeInt(this.category_id);
        dest.writeInt(this.order_count);
        dest.writeString(this.foodImageUrl);
    }

    public Food() {
    }

    protected Food(Parcel in) {
        this.id = in.readInt();
        this.title = in.readString();
        this.price = in.readInt();
        this.content = in.readString();
        this.special = in.readInt();
        this.category_id = in.readInt();
        this.order_count = in.readInt();
        this.foodImageUrl = in.readString();
    }

    public static final Parcelable.Creator<Food> CREATOR = new Parcelable.Creator<Food>() {
        @Override
        public Food createFromParcel(Parcel source) {
            return new Food(source);
        }

        @Override
        public Food[] newArray(int size) {
            return new Food[size];
        }
    };
}
