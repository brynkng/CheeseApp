package com.cheeseapp.DbAdapter;

import android.content.Context;
import android.database.Cursor;
import database.MySqlLiteBuilder;

/**
 * User: Bryan King
 * Date: 4/27/12
 */
public class IngredientDbAdapter extends DbAdapter{

    public static final String TABLE = "ingredients";
    public static final String KEY_ID = "_id";
    public static final String KEY_NAME = "name";
    public static final String KEY_MEASUREMENT_NAME = "measurement_name";

    public static final String LINKER_TABLE = "recipe_ingredients";
    public static final String LINKER_KEY_QUANTITY = "quantity";
    public static final String LINKER_KEY_INGREDIENT_ID = "ingredient_id";
    public static final String LINKER_KEY_DIRECTION_ID = "direction_id";
    public static final String LINKER_KEY_RECIPE_ID = "recipe_id";

    @Override
    public void prePopulate() {
        this.mDb.execSQL("delete from " + TABLE);
        this.mDb.execSQL("INSERT INTO directions (time, yield) values (7, 2)");
    }

    public IngredientDbAdapter(Context ctx) {
        super(ctx);
    }

    public Cursor getIngredientsForRecipe(long recipeId) {
        MySqlLiteBuilder sqlLiteBuilder = new MySqlLiteBuilder();
        sqlLiteBuilder.setTables(TABLE + " JOIN " + LINKER_TABLE + " ON (" + LINKER_KEY_INGREDIENT_ID + " = _id)");

        return sqlLiteBuilder.query(
                mDb,
                new String[] {KEY_ID, KEY_NAME, LINKER_KEY_QUANTITY, KEY_MEASUREMENT_NAME},
                LINKER_KEY_RECIPE_ID + " = ?",
                new String[] {Long.toString(recipeId)}
        );
    }
    
    public Cursor getIngredientsForRecipeDirection(long directionId) {
        MySqlLiteBuilder sqlLiteBuilder = new MySqlLiteBuilder();
        sqlLiteBuilder.setTables(TABLE + " JOIN " + LINKER_TABLE + " ON (" + LINKER_KEY_INGREDIENT_ID + " = _id)");

        return sqlLiteBuilder.query(
                mDb,
                new String[] {KEY_ID, KEY_NAME, LINKER_KEY_QUANTITY, KEY_MEASUREMENT_NAME},
                LINKER_KEY_DIRECTION_ID + " = ? ",
                new String[] {Long.toString(directionId)}
        );
    }
}
