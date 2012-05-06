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
    public static final String KEY_YIELD = "yield";

    @Override
    public void prePopulate() {
        this.mDb.execSQL("delete from " + TABLE);
        this.mDb.execSQL("INSERT INTO directions (time, yield) values (7, 2)");
    }

    public RecipeDbAdapter(Context ctx) {
        super(ctx);
    }

    public Cursor getRecipeForCheese(long cheeseId) {
        return this.mDb.query(
                TABLE,
                new String[] {KEY_ID, KEY_TIME, KEY_YIELD},
                "cheese_id = ?",
                new String[] {Long.toString(cheeseId)}, null, null, null
        );
    }
}
