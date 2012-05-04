package com.cheeseapp.DbAdapter;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import database.MySqlLiteBuilder;

/**
 * User: Bryan King
 * Date: 4/27/12
 */
public class CheeseTypeDbAdapter extends DbAdapter{

    public static final String TABLE = "cheese_types";

    public static final String KEY_ID = "cheese_type_id";
    public static final String KEY_TYPE = "type";

    @Override
    public void prePopulate() {
        this.mDb.execSQL("DELETE FROM " + "cheese_types");
        this.mDb.execSQL("delete from sqlite_sequence where name=" + "'" + "cheese_types" + "'");

        this.createCheeseType("Hard");
        this.createCheeseType("Soft");
        this.createCheeseType("Creamy");

        this.mDb.execSQL("DELETE FROM " + "cheese_types_to_cheese");
        this.mDb.execSQL("delete from sqlite_sequence where name=" + "'" + "cheese_types_to_cheese" + "'");
        this.addTypeToCheese(1, 1);
        this.addTypeToCheese(2, 2);
        this.addTypeToCheese(2, 3);
        this.addTypeToCheese(3, 1);
    }

    public CheeseTypeDbAdapter(Context ctx) {
        super(ctx);
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
                "USING (cheese_type_id)"
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
        insert.put(CheeseDbAdapter.KEY_ID, cheeseId);
        insert.put(KEY_TYPE, cheeseTypeId);

        return this.mDb.insert(TABLE, null, insert);
    }
}
