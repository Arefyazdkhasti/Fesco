package com.example.fesco.OpenHelper;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import androidx.annotation.Nullable;

import com.example.fesco.classes.Comment;

import java.util.ArrayList;
import java.util.List;

public class CommentsDataBaseOpenHelper extends SQLiteOpenHelper {

    Context context;
    public static final String TAG = "DatabaseOpenHelper";
    public static final String DATABASE_NAME = "fesco";
    public static final int DATABASE_VERSION = 1;
    public static final String TABLE_NAME = "comments";

    public static final String COL_ID = "col_id";
    public static final String COL_USER_ID = "col_user_id";
    public static final String COL_USERNAME = "col_username";
    public static final String COL_TITLE = "col_title";
    public static final String COL_CONTENT = "col_content";
    public static final String COL_DATE = "col_date";
    public static final String COL_FOOD_ID = "col_food_id";


    private static final String SQL_COMMAND_CREATE_COMMENTS_TABLE = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME + " (" +
            COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            COL_USER_ID + " INTEGER," +
            COL_USERNAME + " INTEGER," +
            COL_TITLE + " TEXT, " +
            COL_CONTENT + " INTEGER, " +
            COL_DATE + " DATETIME, " +
            COL_FOOD_ID + " INTEGER);";


    public CommentsDataBaseOpenHelper(@Nullable Context context) {
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
    }


    public boolean addComment(Comment comment) {
        ContentValues cv = new ContentValues();
        cv.put(COL_TITLE, comment.getTitle());
        cv.put(COL_CONTENT, comment.getContent());
        cv.put(COL_USERNAME, comment.getUsername());
        cv.put(COL_DATE, comment.getDate());
        cv.put(COL_USER_ID, comment.getUser_id());
        cv.put(COL_FOOD_ID, comment.getFood_related_id());

        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();

        long isInserted = sqLiteDatabase.insert(TABLE_NAME, null, cv);

        Log.i(TAG, "addPost: " + isInserted);
        if (isInserted > 0) {
            return true;
        } else {
            return false;
        }
    }

    public void addComments(List<Comment> comments) {
        for (int i = 0; i < comments.size(); i++) {
            if (!checkCommentExists(comments.get(i).getId())) {
                addComment(comments.get(i));
            }
        }
    }

    public List<Comment> getComments() {
        List<Comment> comments = new ArrayList<>();
        SQLiteDatabase sqLiteDatabase = this.getReadableDatabase();
        Cursor cursor = sqLiteDatabase.rawQuery("SELECT * FROM " + TABLE_NAME, null);
        cursor.moveToFirst();
        if (cursor.getCount() > 0) {
            while (!cursor.isAfterLast()) {
                Comment comment = new Comment();
                comment.setId(cursor.getInt(0));
                comment.setUser_id(cursor.getInt(1));
                comment.setUsername(cursor.getString(2));
                comment.setTitle(cursor.getString(3));
                comment.setContent(cursor.getString(4));
                comment.setDate(cursor.getString(5));
                comment.setFood_related_id(cursor.getInt(6));

                comments.add(comment);
                //برای اینکه لوپ بتونه بسته شه
                cursor.moveToNext();
            }
        }
        cursor.close();
        sqLiteDatabase.close();
        return comments;
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
