package com.cheeseapp.DbAdapters;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * User: Bryan King
 * Date: 4/21/12
 */
public class CheeseDbAdapter extends DbAdapter{

    public static final String KEY_CHEESE_ID = "cheese_id";
    public static final String KEY_NAME = "name";
    public static final String KEY_CHEESE_TYPE_ID = "cheese_type_id";

    private static final String DATABASE_CREATE =
            "CREATE TABLE IF NOT EXISTS cheeses"
            + "("
            +   "cheese_id SERIAL,"
            +   "cheese_type_id int(11)"
            + ")";

    private static final String DATABASE_TABLE = "cheeses";


    /**
     * Constructor - takes the context to allow the database to be
     * opened/created
     *
     * @param ctx the Context within which to work
     */
    public CheeseDbAdapter(Context ctx) {
        super(ctx, DATABASE_CREATE, DATABASE_TABLE);
    }

    /**
     * @param name
     * @param cheeseTypeId
     * @return cheeseId or -1 if failed
     */
    public long createCheese(String name, int cheeseTypeId) {
        ContentValues initialValues = new ContentValues();
        initialValues.put(KEY_NAME, name);
        initialValues.put(KEY_CHEESE_TYPE_ID, cheeseTypeId);

        return this.mDb.insert(DATABASE_TABLE, null, initialValues);
    }

    /**
     * @param cheeseId
     * @return true if deleted, false otherwise
     */
    public boolean deleteCheese(long cheeseId) {
        return this.mDb.delete(DATABASE_TABLE, KEY_CHEESE_ID + "=" + cheeseId, null) > 0;
    }

    /**
     * Return a Cursor over the list of all cheeses in the database
     *
     * @return Cursor over all cheeses
     */
    public Cursor getAllCheeses() {
        return this.mDb.query(DATABASE_TABLE, new String[] {KEY_CHEESE_ID, KEY_NAME,
                KEY_CHEESE_TYPE_ID}, null, null, null, null, null);
    }

    /**
     * Return a Cursor positioned at the cheese that matches the given cheeseId
     *
     * @param cheeseId
     * @return Cursor positioned to matching cheese, if found
     * @throws SQLException if cheese could not be found/retrieved
     */
    public Cursor getCheese(long cheeseId) throws SQLException {
        Cursor mCursor =
                this.mDb.query(true, DATABASE_TABLE, new String[] {KEY_CHEESE_ID,
                        KEY_NAME, KEY_CHEESE_TYPE_ID}, KEY_CHEESE_ID + "=" + cheeseId, null,
                        null, null, null, null);
        if (mCursor != null) {
            mCursor.moveToFirst();
        }
        return mCursor;

    }
}
