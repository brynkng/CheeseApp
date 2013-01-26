package com.cheeseapp.DbAdapter;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

/**
 * User: Bryan King
 * Date: 4/27/12
 */
public class DirectionDbAdapter {

    public static final String TABLE = "directions";
    public static final String KEY_ID = "_id";
    public static final String KEY_DIRECTION = "direction";
    public static final String KEY_RECIPE_ID = "recipe_id";
    public static final String KEY_DIRECTION_CATEGORY_ID = "direction_category_id";
    private SQLiteDatabase mDb;

    public DirectionDbAdapter(Context ctx) {
        mDb = DbAdapter.getDbInstance(ctx);
    }

    public Cursor getDirectionsForRecipe(long recipeId) {
        Cursor cursor = this.mDb.query(
                TABLE,
                new String[] {KEY_ID, KEY_DIRECTION, KEY_DIRECTION_CATEGORY_ID},
                KEY_RECIPE_ID + " = ?",
                new String[] {Long.toString(recipeId)}, null, null, null
        );

        cursor.moveToFirst();

        return cursor;
    }
}
