package com.cheeseapp.DbAdapter;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

/**
 * User: Bryan King
 * Date: 4/27/12
 */
public class RecipeDbAdapter {

    public static final String TABLE = "recipes";
    public static final String KEY_ID = "_id";
    public static final String KEY_TIME = "time";
    public static final String KEY_CHEESE_ID = "cheese_id";
    public static final String KEY_YIELD = "yield";
    private SQLiteDatabase mDb;

    public RecipeDbAdapter(Context ctx) {
        mDb = DbAdapter.getDbInstance(ctx);
    }

    public Cursor getRecipeForCheese(long cheeseId) {
        Cursor cursor =  this.mDb.query(
                TABLE,
                new String[] {KEY_ID, KEY_TIME, KEY_YIELD},
                KEY_CHEESE_ID + " = ?",
                new String[] {Long.toString(cheeseId)}, null, null, null
        );

        cursor.moveToFirst();

        return cursor;
    }
}
