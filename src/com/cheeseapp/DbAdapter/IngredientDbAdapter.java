package com.cheeseapp.DbAdapter;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import database.MySqlLiteBuilder;

/**
 * User: Bryan King
 * Date: 4/27/12
 */
public class IngredientDbAdapter {

    public static final String TABLE = "ingredients";
    public static final String KEY_ID = "_id";
    public static final String KEY_NAME = "name";
    public static final String KEY_UNIT = "unit";

    public static final String LINKER_TABLE = "recipe_ingredients";
    public static final String LINKER_KEY_QUANTITY = "quantity";
    public static final String LINKER_KEY_INGREDIENT_ID = "ingredient_id";
    public static final String LINKER_KEY_DIRECTION_ID = "direction_id";
    public static final String LINKER_KEY_RECIPE_ID = "recipe_id";
    private SQLiteDatabase mDb;

    public IngredientDbAdapter(Context ctx) {
        mDb = DbAdapter.getDbInstance(ctx);
    }

    public Cursor getIngredientsForRecipe(long recipeId) {
        MySqlLiteBuilder sqlLiteBuilder = new MySqlLiteBuilder();
        sqlLiteBuilder.setTables(TABLE + " JOIN " + LINKER_TABLE + " ON (" + LINKER_KEY_INGREDIENT_ID + " = _id)");

        return sqlLiteBuilder.query(
                mDb,
                new String[] {KEY_ID, KEY_NAME, LINKER_KEY_QUANTITY, KEY_UNIT},
                LINKER_KEY_RECIPE_ID + " = ?",
                new String[] {Long.toString(recipeId)}
        );
    }
    
    public Cursor getIngredientsForRecipeDirection(long directionId) {
        MySqlLiteBuilder sqlLiteBuilder = new MySqlLiteBuilder();
        sqlLiteBuilder.setTables(TABLE + " JOIN " + LINKER_TABLE + " ON (" + LINKER_KEY_INGREDIENT_ID + " = _id)");

        return sqlLiteBuilder.query(
                mDb,
                new String[] {KEY_ID, KEY_NAME, LINKER_KEY_QUANTITY, KEY_UNIT},
                LINKER_KEY_DIRECTION_ID + " = ? ",
                new String[] {Long.toString(directionId)}
        );
    }
}
