package com.example.fesco.OpenHelper;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.exmaple.fesco.Login.SQLiteHandler;

import java.util.HashMap;

public class AddToCardDataBaseOpenHelper extends SQLiteOpenHelper {

    private static final String TAG = SQLiteHandler.class.getSimpleName();


    private static final int DATABASE_VERSION = 1;

    private static final String DATABASE_NAME = "fesco_database_orders";

    private static final String TABLE_CART = "orders";

    private static final String KEY_ID = "id";
    private static final String KEY_USER_ID = "user_id";
    private static final String KEY_FOOD_ID = "food_id";
    private static final String KEY_FOOD_COUNT = "food_count";
    private static final String KEY_PRICE = "price";
    private static final String KEY_STATUS="status";
    private static final String KEY_CREATED_AT="created_at";

    public AddToCardDataBaseOpenHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }


    @Override
    public void onCreate(SQLiteDatabase db) {


        String CREATE_LOGIN_TABLE = "CREATE TABLE " + TABLE_CART + "("
                + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + KEY_USER_ID + " INTEGER,"
                + KEY_FOOD_ID + " INTEGER,"
                + KEY_FOOD_COUNT + " INTEGER,"
                + KEY_PRICE + " INTEGER,"
                + KEY_STATUS + " INTEGER,"
                + KEY_CREATED_AT + " TEXT" + ")";

        db.execSQL(CREATE_LOGIN_TABLE);

        Log.d(TAG, "Database tables created");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CART);
        onCreate(db);
    }


    public void addItemToCard(String user_id,String food_id,String food_count,String price,String status,String created_at) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_USER_ID, user_id);
        values.put(KEY_FOOD_ID, food_id);
        values.put(KEY_FOOD_COUNT, food_count);
        values.put(KEY_PRICE, price);
        values.put(KEY_STATUS, status);
        values.put(KEY_CREATED_AT, created_at); // Created At

        // Inserting Row
        long id = db.insert(TABLE_CART, null, values);
        db.close(); // Closing database connection

        Log.d(TAG, "New user inserted into SQLite: " + id);
    }


    public HashMap<String, String> getCardDetails() {
        HashMap<String, String> item = new HashMap<>();
        String selectQuery = "SELECT  * FROM " + TABLE_CART;

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        // Move to first row
        cursor.moveToFirst();
        if (cursor.getCount() > 0) {
            item.put("id", cursor.getString(0));
            item.put("user_id", cursor.getString(1));
            item.put("food_id", cursor.getString(2));
            item.put("food_count", cursor.getString(3));
            item.put("price",cursor.getString(4));
            item.put("status",cursor.getString(5));
        }
        cursor.close();
        db.close();
        // return user
        Log.d(TAG, "Fetching item from SQLite: " + item.toString());

        return item;
    }

    public void deleteUsers() {
        SQLiteDatabase db = this.getWritableDatabase();
        // Delete All Rows
        db.delete(TABLE_CART, null, null);
        db.close();

        Log.d(TAG, "Deleted all user info from SQLite");
    }
}

