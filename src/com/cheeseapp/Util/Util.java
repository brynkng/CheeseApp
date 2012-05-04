package com.cheeseapp.Util;

import android.content.Context;
import android.database.Cursor;

/**
 * User: Bryan King
 * Date: 4/29/12
 */
public class Util {
    
    public static int getImageResourceFromCursor(Context context, Cursor cursor, int columnIndex) {
        String cheeseName = cursor.getString(columnIndex);
        String imgName = cheeseName.replace(" ", "_").toLowerCase();

        return context.getResources().getIdentifier("com.cheeseapp" + ":drawable/" + imgName, null, null);
    }
}
