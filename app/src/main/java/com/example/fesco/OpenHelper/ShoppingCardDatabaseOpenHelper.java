package com.example.fesco.OpenHelper;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.example.fesco.classes.ShoppingCard;

import java.util.ArrayList;
import java.util.List;

public class ShoppingCardDatabaseOpenHelper extends SQLiteOpenHelper {

    Context context;
    public static final String TAG = "DatabaseOpenHelper";
    public static final String DATABASE_NAME = "fesco_db";
    public static final int DATABASE_VERSION = 1;
    public static final String TABLE_NAME = "orders";

    public static final String COL_ID = "col_id";
    public static final String COL_USER_ID = "col_user_id";
    public static final String COL_FOOD_ID = "col_food_id";
    public static final String COL_FOOD_COUNT = "col_food_count";
    public static final String COL_PRICE = "col_price";
    public static final String COL_STATUS = "col_status";
    public static final String COL_CREATED_AT = "col_created_at";


    private static final String SQL_COMMAND_CREATE_COMMENTS_TABLE = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME + " (" +
            COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            COL_USER_ID + " INTEGER," +
            COL_FOOD_ID + " INTEGER," +
            COL_FOOD_COUNT + " INTEGER," +
            COL_PRICE + " TEXT, " +
            COL_STATUS + " INTEGER, " +
            COL_CREATED_AT + " TEXT);";


    public ShoppingCardDatabaseOpenHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        try {
            db.execSQL(SQL_COMMAND_CREATE_COMMENTS_TABLE);
        } catch (SQLException e) {
            Log.e(TAG, "onCreate: " + e.toString());
        }
    }

    @Override
    public void onOpen(SQLiteDatabase db) {
        onCreate(db);
    }


    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onCreate(db);
    }

    @Override
    public void onConfigure(SQLiteDatabase db) {
        super.onConfigure(db);
        onCreate(db);
    }

    public void updateStatus(int status, int id) {
        SQLiteDatabase db = this.getWritableDatabase();
       /* String query = "UPDATE " + TABLE_NAME + " SET status=" + "'" + status + "'" + " WHERE id=" + id;

        db.execSQL(query);*/

        db.execSQL("UPDATE " + TABLE_NAME +
                " SET " + COL_STATUS + "=" + status +
                " WHERE " + COL_ID + "=" + id);

        Toast.makeText(context, "Status chnaged", Toast.LENGTH_SHORT).show();
    }


    public boolean addShoppingCardItem(ShoppingCard shoppingCards) {
        ContentValues cv = new ContentValues();
        cv.put(COL_USER_ID, shoppingCards.getUser_id());
        cv.put(COL_FOOD_ID, shoppingCards.getFood_id());
        cv.put(COL_FOOD_COUNT, shoppingCards.getFood_count());
        cv.put(COL_PRICE, shoppingCards.getFinal_price());
        cv.put(COL_STATUS, shoppingCards.getStatus());
        cv.put(COL_CREATED_AT, shoppingCards.getCreated_at());

        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();

        long isInserted = sqLiteDatabase.insert(TABLE_NAME, null, cv);

        Log.i(TAG, "addShoppingCardItem: " + isInserted);
        if (isInserted > 0) {
            return true;
        } else {
            return false;
        }
    }

    public void addShoppingCardItems(List<ShoppingCard> shoppingCards) {
        for (int i = 0; i < shoppingCards.size(); i++) {
            if (!checkCommentExists(shoppingCards.get(i).getId())) {
                addShoppingCardItem(shoppingCards.get(i));
            }
        }
    }

    public List<ShoppingCard> getShoppingCardItems() {
        List<ShoppingCard> shoppingCards = new ArrayList<>();
        SQLiteDatabase sqLiteDatabase = this.getReadableDatabase();
        Cursor cursor = sqLiteDatabase.rawQuery("SELECT * FROM " + TABLE_NAME, null);
        cursor.moveToFirst();
        if (cursor.getCount() > 0) {
            while (!cursor.isAfterLast()) {
                ShoppingCard shoppingCard = new ShoppingCard();
                shoppingCard.setId(cursor.getInt(0));
                shoppingCard.setUser_id(cursor.getInt(1));
                shoppingCard.setFood_id(cursor.getInt(2));
                shoppingCard.setFood_count(cursor.getInt(3));
                shoppingCard.setFinal_price(cursor.getInt(4));
                shoppingCard.setStatus(cursor.getInt(5));
                shoppingCard.setCreated_at(cursor.getString(6));

                shoppingCards.add(shoppingCard);
                //برای اینکه لوپ بتونه بسته شه
                cursor.moveToNext();
            }
        }
        cursor.close();
        sqLiteDatabase.close();
        return shoppingCards;
    }


    private boolean checkCommentExists(int postId) {
        SQLiteDatabase sqLiteDatabase = this.getReadableDatabase();
        Cursor cursor = sqLiteDatabase.rawQuery("SELECT * FROM "
                + TABLE_NAME
                + " WHERE "
                + COL_ID +
                " = ?", new String[]{String.valueOf(postId)});
        return cursor.moveToFirst();
    }

}

