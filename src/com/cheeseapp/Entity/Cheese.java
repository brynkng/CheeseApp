package com.cheeseapp.Entity;

import android.content.Context;
import android.database.Cursor;
import com.cheeseapp.DbAdapter.CheeseDbAdapter;

/**
 * User: Bryan King
 * Date: 4/28/12
 */
public class Cheese {

    private long cheeseId;
    private String name;
    private boolean isFavorite;

    public Cheese(Context context, long cheeseId) {
        CheeseDbAdapter dbAdapter = new CheeseDbAdapter(context);
        Cursor CheeseCursor = dbAdapter.getCheese(cheeseId);
        
        this.cheeseId = cheeseId;
        this.name = CheeseCursor.getString(CheeseCursor.getColumnIndexOrThrow(CheeseDbAdapter.KEY_NAME));
        this.isFavorite = (CheeseCursor.getInt(CheeseCursor.getColumnIndexOrThrow(CheeseDbAdapter.KEY_FAVORITE)) == 1);
    }
    
    public boolean isFavorite() {
        return isFavorite;
    }

    public String getName() {
        return name;
    }

    public long getId() {
        return cheeseId;
    }
}
