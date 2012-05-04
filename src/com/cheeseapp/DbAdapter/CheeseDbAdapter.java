package com.cheeseapp.DbAdapter;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;

/**
 * User: Bryan King
 * Date: 4/21/12
 */
public class CheeseDbAdapter extends DbAdapter{

    public static final String KEY_ID = "_id";
    public static final String KEY_NAME = "name";
    public static final String KEY_FAVORITE = "favorite";
    public static final String KEY_DESCRIPTION = "description";

    public static final String TABLE = "cheeses";

    /**
     * @param ctx the Context within which to work
     */
    public CheeseDbAdapter(Context ctx) {
        super(ctx);
    }

    @Override
    public void prePopulate() {
        this.mDb.execSQL("delete from cheeses");
        this.mDb.execSQL("delete from sqlite_sequence where name=" + "'" + TABLE + "'");
        this.createCheese("Cheddar", "Cheddar is awesome! I'm glad you agree!");
        this.createCheese("Gouda", "I am a gouda cheese suck it!");
        this.createCheese("Swiss", "I am the holiest of all the cheeses");
        this.createCheese("Blue", "I am the strongest of all the cheeses");
    }

    /**
     * Create a new cheese. Make sure the picture file has the exact same name as the one provided here.
     *
     * @param name
     * @return cheeseId or -1 if failed
     */
    public long createCheese(String name, String description) {
        ContentValues initialValues = new ContentValues();
        initialValues.put(KEY_NAME, name);
        initialValues.put(KEY_DESCRIPTION, description);

        return this.mDb.insert(TABLE, null, initialValues);
    }

    /**
     * @param cheeseId
     * @return true if deleted, false otherwise
     */
    public boolean deleteCheese(long cheeseId) {
        return this.mDb.delete(TABLE, KEY_ID + "=" + cheeseId, null) > 0;
    }

    /**
     * Return a Cursor over the list of all cheeses in the database
     *
     * @return Cursor over all cheeses
     */
    public Cursor getAllCheeses() {
        return this.mDb.query(TABLE, this._getCheeseFields(), null, null, null, null, null);
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
                this.mDb.query(true, TABLE, this._getCheeseFields(), KEY_ID + "=" + cheeseId, null,
                        null, null, null, null);
        if (mCursor != null) {
            mCursor.moveToFirst();
        }
        return mCursor;

    }

    public boolean isFavorite(long cheeseId) {
        Cursor cheeseCursor = this.getCheese(cheeseId);
        return cheeseCursor.getInt(cheeseCursor.getColumnIndex(KEY_FAVORITE)) == 1;
    }

    public long toggleFavorite(long cheeseId) {
        ContentValues updateValue = new ContentValues();

        int newFavoriteValue;
        if (this.isFavorite(cheeseId)) {
            newFavoriteValue = 0;
        } else {
            newFavoriteValue = 1;
        }

        updateValue.put(KEY_FAVORITE, newFavoriteValue);

        return this.mDb.update(TABLE, updateValue, KEY_ID + " = ?", new String[] {Long.toString(cheeseId)});
    }

    /**
     * Return a Cursor over the list of all favorite cheeses in the database
     *
     * @return Cursor over all cheeses
     */
    public Cursor getFavoriteCheeses() {
        return this.mDb.query(TABLE, this._getCheeseFields(), "favorite = 1", null, null, null, null, null );
    }

    private String[] _getCheeseFields() {
        return new String[] {KEY_ID, KEY_NAME, KEY_DESCRIPTION, KEY_FAVORITE};
    }
}
