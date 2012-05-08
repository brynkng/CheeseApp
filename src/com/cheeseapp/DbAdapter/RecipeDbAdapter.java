package com.cheeseapp.DbAdapter;

import android.content.Context;
import android.database.Cursor;

/**
 * User: Bryan King
 * Date: 4/27/12
 */
public class RecipeDbAdapter extends DbAdapter{

    public static final String TABLE = "recipes";
    public static final String KEY_ID = "_id";
    public static final String KEY_TIME = "time";
    public static final String KEY_CHEESE_ID = "cheese_id";
    public static final String KEY_YIELD = "yield";

    @Override
    public void prePopulate() {
        this.mDb.execSQL("delete from " + TABLE);
        this.mDb.execSQL("delete from sqlite_sequence where name=" + "'" + TABLE + "'");
        this.mDb.execSQL("INSERT INTO recipes (cheese_id, time, yield) values (1, 7, 2)");
    }

    public RecipeDbAdapter(Context ctx) {
        super(ctx);
    }

    public Cursor getRecipeForCheese(long cheeseId) {
        return this.mDb.query(
                TABLE,
                new String[] {KEY_ID, KEY_TIME, KEY_YIELD},
                KEY_CHEESE_ID + " = ?",
                new String[] {Long.toString(cheeseId)}, null, null, null
        );
    }
}
