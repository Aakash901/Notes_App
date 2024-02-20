package com.example.notes_app.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.example.notes_app.utils.Toaster;


public class MyDatabaseHelper extends SQLiteOpenHelper {

    // declaring variables
    private Context context;
    private Toaster toaster = new Toaster();


    private static final String DATABASE_NAME_PREFIX = "UserNotes_";
    private static final int DATABASE_VERSION = 1;

    private static final String TABLE_NAME = "my_library";
    private static final String COLUMN_ID = "_id";
    private static final String COLUMN_TITLE = "note_title";
    private static final String COLUMN_CONTENT = "note_content";
    private static final String COLUMN_DATE = "note_date";

    private static final String COLUMN_USER_ID = "user_id";


    // constructor
    public MyDatabaseHelper(@Nullable Context context, String userId) {
        super(context, DATABASE_NAME_PREFIX + userId, null, DATABASE_VERSION);
        this.context = context;
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        String query = "CREATE TABLE " + TABLE_NAME +
                " (" + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_USER_ID + " TEXT, " +
                COLUMN_TITLE + " TEXT, " +
                COLUMN_CONTENT + " TEXT, " +
                COLUMN_DATE + " TEXT);";
        db.execSQL(query);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);

    }

    // method add notes every time when user add note
    public void addNote(String userId, String title, String content, String date) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();

        cv.put(COLUMN_USER_ID, userId);
        cv.put(COLUMN_TITLE, title);
        cv.put(COLUMN_CONTENT, content);
        cv.put(COLUMN_DATE, date);

        long result = db.insert(TABLE_NAME, null, cv);
        if (result == -1) {
            Toast.makeText(context, "Failed to add note", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(context, "Note added successfully", Toast.LENGTH_SHORT).show();
        }
    }


    // method to update the db
    public void updateNote(String rowId, String title, String content, String date) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();

        cv.put(COLUMN_TITLE, title);
        cv.put(COLUMN_CONTENT, content);
        cv.put(COLUMN_DATE, date);

        db.beginTransaction();
        try {
            long result = db.update(TABLE_NAME, cv, "_id=?", new String[]{rowId});
            if (result == -1) {
                toaster.showToast(context, "Failed to update note");
            } else {
                toaster.showToast(context, "Note updated successfully");
            }
            db.setTransactionSuccessful();
        } catch (Exception e) {
            e.printStackTrace();
            toaster.showToast(context, "Error updating note: " + e.getMessage());
        } finally {
            db.endTransaction();
        }
    }


    // method to delete the particular note from database

    public void deleteOneNote(String rowId) {
        SQLiteDatabase db = this.getWritableDatabase();
        long result = db.delete(TABLE_NAME, "_id=?", new String[]{rowId});
        if (result == -1) {
            toaster.showToast(context, "Failed to delete note");
        } else {
            toaster.showToast(context, "Note deleted");
        }
    }

    public void deleteAllNotes() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM " + TABLE_NAME);
    }

    // method to get all data based on userId
    public Cursor readAllData(String userId) {
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "SELECT * FROM " + TABLE_NAME + " WHERE " + COLUMN_USER_ID + " = ?";

        try {
            // Use rawQuery with selectionArgs to avoid SQL injection
            String[] selectionArgs = {userId};
            return db.rawQuery(query, selectionArgs);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }


}
