package com.cheeseapp.DbAdapter;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import database.MySqlLiteBuilder;

/**
 * User: Bryan King
 * Date: 4/27/12
 */
public class DirectionDbAdapter extends DbAdapter{

    public static final String TABLE = "directions";
    public static final String KEY_ID = "_id";
    public static final String KEY_DIRECTION = "direction";
    public static final String KEY_RECIPE_ID = "recipe_id";
    public static final String KEY_DIRECTION_CATEGORY_ID = "direction_category_id";

    @Override
    public void prePopulate() {
        this.mDb.execSQL("delete from " + TABLE);
        this.mDb.execSQL("INSERT INTO directions (direction, direction_category_id) values (1, 'Heat the milk. Drumstick jerky chicken filet mignon. Jerky flank pork belly capicola, pancetta cow boudin speck venison fatback kielbasa pork chop t-bone shoulder biltong.', 1)");
        this.mDb.execSQL("INSERT INTO directions (direction, direction_category_id) values (1, 'Add the rennet.', 2)");
        this.mDb.execSQL("INSERT INTO directions (direction, direction_category_id) values (1, 'Cut the curds. Beef shoulder strip steak, tri-tip frankfurter ribeye pork tongue spare ribs capicola jowl turducken pig speck biltong. Filet mignon short ribs speck, kielbasa hamburger turkey pastrami swine pancetta salami. Flank ground round biltong meatball. Jerky beef ribs hamburger, ham hock meatball tenderloin jowl filet mignon drumstick turkey. Capicola chuck shoulder, hamburger swine strip steak ham hock biltong chicken rump.', 2)");
        this.mDb.execSQL("INSERT INTO directions (direction, direction_category_id) values (1, 'Press that shit real good. ndouille shankle brisket, pork loin kielbasa tongue filet mignon rump jowl drumstick. Brisket turducken rump strip steak flank. ', 3)");
    }

    public DirectionDbAdapter(Context ctx) {
        super(ctx);
    }

//    public Cursor getDirection(long directionId) {
//
//    }

    public Cursor getDirectionsForRecipe(long recipeId) {
        return this.mDb.query(
                TABLE,
                new String[] {KEY_ID, KEY_DIRECTION, KEY_DIRECTION_CATEGORY_ID},
                KEY_RECIPE_ID + " = ?",
                new String[] {Long.toString(recipeId)}, null, null, null
        );
    }
}
