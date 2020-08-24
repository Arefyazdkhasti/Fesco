package com.example.fesco.OpenHelper;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Build;
import android.util.Log;

import androidx.annotation.Nullable;

import com.example.fesco.classes.Category;
import com.example.fesco.classes.Food;

import java.util.ArrayList;
import java.util.List;

public class DataBaseOpenHelper extends SQLiteOpenHelper {


    Context context;
    public static final String TAG = "DatabaseOpenHelper";
    public static final String DATABASE_NAME = "fesco_db";
    public static final int DATABASE_VERSION = 1;
    public static final String FOOD_TABLE_NAME = "foods";

    public static final String COL_ID = "col_id";
    public static final String COL_NAME = "col_name";
    public static final String COL_PRICE = "col_price";
    public static final String COL_COMPOUNDS = "col_compounds";
    public static final String COL_SPECIAL = "col_special";
    public static final String COL_CATEGORY_ID = "col_category_id";
    public static final String COL_ORDER_COUNT = "col_order_count";
    public static final String COL_FILE_NAME = "col_file_name";


    public static final String CAT_TABLE_NAME = "categories";

    public static final String COL_ID_CAT = "col_id";
    public static final String COL_NAME_CAT = "col_name";

    private static final String SQL_COMMAND_CREATE_FOODS_TABLE = "CREATE TABLE IF NOT EXISTS " + FOOD_TABLE_NAME + " (" +
            COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            COL_NAME + " TEXT," +
            COL_PRICE + " INTEGER," +
            COL_COMPOUNDS + " TEXT, " +
            COL_SPECIAL + " INTEGER, " +
            COL_CATEGORY_ID + " INTEGER, " +
            COL_ORDER_COUNT + " INTEGER, " +
            COL_FILE_NAME + " TEXT );";

    private static final String SQL_COMMAND_CREATE_CAT_TABLE = "CREATE TABLE IF NOT EXISTS " + CAT_TABLE_NAME + " (" +
            COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            COL_NAME_CAT + " TEXT );";


    public DataBaseOpenHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        try {
            db.execSQL(SQL_COMMAND_CREATE_FOODS_TABLE);
            db.execSQL(SQL_COMMAND_CREATE_CAT_TABLE);
        } catch (SQLException e) {
            Log.e(TAG, "onCreate: " + e.toString());
        }
    }

    @Override
    public void onOpen(SQLiteDatabase db) {
        super.onOpen(db);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            db.disableWriteAheadLogging();
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public boolean addFood(Food food) {
        ContentValues cv = new ContentValues();
        cv.put(COL_FILE_NAME, food.getFoodImageUrl());
        cv.put(COL_NAME, food.getTitle());
        cv.put(COL_COMPOUNDS, food.getContent());

        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();

        long isInserted = sqLiteDatabase.insert(FOOD_TABLE_NAME, null, cv);

        Log.i(TAG, "addPost: " + isInserted);
        if (isInserted > 0) {
            return true;
        } else {
            return false;
        }
    }

    public void addFoods(List<Food> foods) {
        for (int i = 0; i < foods.size(); i++) {
            if (!checkPostExists(foods.get(i).getId())) {
                addFood(foods.get(i));
            }
        }
    }

    public List<Food> getFoods() {
        List<Food> foods = new ArrayList<>();
        SQLiteDatabase sqLiteDatabase = this.getReadableDatabase();
        Cursor cursor = sqLiteDatabase.rawQuery("SELECT * FROM " + FOOD_TABLE_NAME, null);
        cursor.moveToFirst();
        if (cursor.getCount() > 0) {
            while (!cursor.isAfterLast()) {
                Food food = new Food();
                food.setId(cursor.getInt(0));
                food.setTitle(cursor.getString(1));
                food.setPrice(cursor.getInt(2));
                food.setContent(cursor.getString(3));
                food.setSpecial(cursor.getInt(4));
                food.setCategory_id(cursor.getInt(5));
                food.setOrder_count(cursor.getInt(6));
                food.setFoodImageUrl(cursor.getString(7));

                foods.add(food);
                //برای اینکه لوپ بتونه بسته شه
                cursor.moveToNext();
            }
        }
        cursor.close();
        sqLiteDatabase.close();
        return foods;
    }

    private boolean checkPostExists(int postId) {
        SQLiteDatabase sqLiteDatabase = this.getReadableDatabase();
        Cursor cursor = sqLiteDatabase.rawQuery("SELECT * FROM "
                + FOOD_TABLE_NAME
                + " WHERE "
                + COL_ID +
                " = ?", new String[]{String.valueOf(postId)});
        return cursor.moveToFirst();
    }






    public boolean addCat(Category category) {
        ContentValues cv = new ContentValues();
        cv.put(COL_ID_CAT, category.getId());
        cv.put(COL_NAME, category.getName());


        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();

        long isInserted = sqLiteDatabase.insert(FOOD_TABLE_NAME, null, cv);

        Log.i(TAG, "addPost: " + isInserted);
        if (isInserted > 0) {
            return true;
        } else {
            return false;
        }
    }

    public void addCats(List<Category> categories) {
        for (int i = 0; i < categories.size(); i++) {
            if (!checkPostExists(categories.get(i).getId())) {
                addCat(categories.get(i));
            }
        }
    }

    public List<Category> getCats() {
        List<Category> categories = new ArrayList<>();
        SQLiteDatabase sqLiteDatabase = this.getReadableDatabase();
        Cursor cursor = sqLiteDatabase.rawQuery("SELECT * FROM " + FOOD_TABLE_NAME, null);
        cursor.moveToFirst();
        if (cursor.getCount() > 0) {
            while (!cursor.isAfterLast()) {
                Category category = new Category ();
                category.setId(cursor.getInt(0));
                category.setName(cursor.getString(1));

                categories.add(category);
                //برای اینکه لوپ بتونه بسته شه
                cursor.moveToNext();
            }
        }
        cursor.close();
        sqLiteDatabase.close();
        return categories;
    }

    private boolean checkCatExists(int postId) {
        SQLiteDatabase sqLiteDatabase = this.getReadableDatabase();
        Cursor cursor = sqLiteDatabase.rawQuery("SELECT * FROM "
                + FOOD_TABLE_NAME
                + " WHERE "
                + COL_ID +
                " = ?", new String[]{String.valueOf(postId)});
        return cursor.moveToFirst();
    }
}
