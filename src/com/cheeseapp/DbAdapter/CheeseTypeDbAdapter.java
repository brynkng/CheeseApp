package com.cheeseapp.DbAdapter;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import database.MySqlLiteBuilder;

/**
 * User: Bryan King
 * Date: 4/27/12
 */
public class CheeseTypeDbAdapter {

    public static final String TABLE = "cheese_types";

    public static final String KEY_ID = "_id";
    public static final String KEY_TYPE = "type";
    private SQLiteDatabase mDb;

    public CheeseTypeDbAdapter(Context ctx) {
        mDb = DbAdapter.getDbInstance(ctx);
    }

    /**
     * @return cheeseId or -1 if failed
     * @param cheeseType cheese type name
     */
    public long createCheeseType(String cheeseType) {
        ContentValues initialValues = new ContentValues();
        initialValues.put(KEY_TYPE, cheeseType);

        return this.mDb.insert(TABLE, null, initialValues);
    }

    public boolean deleteCheeseType(long cheeseTypeId) {
        return this.mDb.delete(TABLE, KEY_ID + "=" + cheeseTypeId, null) > 0;
    }

    public Cursor getAllCheeseTypes() {
        return this.mDb.query(TABLE, new String[] {"type_id", KEY_TYPE}, null, null, null, null, null);
    }

    //
    //For a specific cheese
    //

    /**
     * @param cheeseId
     * @return Cursor of all cheese types for a particular cheese
     */
    public Cursor getCheeseTypesForCheese(long cheeseId) {
        MySqlLiteBuilder sqlBuilder = new MySqlLiteBuilder();

        sqlBuilder.setTables(
                "cheese_types " +
                "JOIN cheese_types_to_cheese " +
                "ON (_id = cheese_type_id)"
        );

        return sqlBuilder.query(
                mDb,
                new String[] {KEY_ID, KEY_TYPE},
                "cheese_id = ?",
                new String[] {Long.toString(cheeseId)}
        );
    }

    public long addTypeToCheese(long cheeseId, long cheeseTypeId) {
        ContentValues insert = new ContentValues();
        insert.put("cheese_id", cheeseId);
        insert.put("cheese_type_id", cheeseTypeId);

        return this.mDb.insert("cheese_types_to_cheese", null, insert);
    }
}
