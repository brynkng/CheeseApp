package com.cheeseapp.DbAdapter;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

/**
 * User: Bryan King
 * Date: 4/27/12
 */
public class DirectionCategoryDbAdapter extends DbAdapter{

    //Cheese Types table
    public static final String TABLE = "direction_categories";
    public static final String KEY_ID = "_id";
    public static final String KEY_NAME = "name";

    @Override
    public void prePopulate() {
        this.mDb.execSQL("delete from " + TABLE);
        this.mDb.execSQL("delete from sqlite_sequence where name=" + "'" + TABLE + "'");
        this.createDirectionCategory("Renneting");
        this.createDirectionCategory("Cooking Curds");
        this.createDirectionCategory("Cutting Curds");
        this.createDirectionCategory("Pressing");
        this.createDirectionCategory("Waxing");
        this.createDirectionCategory("Aging");
    }

    public DirectionCategoryDbAdapter(Context ctx) {
        super(ctx);
    }

    /**
     * @return direction category id or -1 if failed
     */
    public long createDirectionCategory(String name) {
        ContentValues initialValues = new ContentValues();
        initialValues.put(KEY_NAME, name);

        return this.mDb.insert(TABLE, null, initialValues);
    }

    public String getDirectionCategoryName(long directionCategoryId) {
        Cursor directionCursor = this.mDb.query(TABLE, new String[] {KEY_NAME}, "_id = ?", new String[] {Long.toString(directionCategoryId)}, null, null, null);
        directionCursor.moveToFirst();

        return directionCursor.getString(directionCursor.getColumnIndexOrThrow(KEY_NAME));
    }

    public Cursor getPossibleDirectionCategoriesForJournal(long journalId) {
        String sql = "SELECT dc.* "
                + "FROM journals j "
                + "JOIN recipes r  USING(cheese_id) "
                + "JOIN directions d  ON(r._id = d.recipe_id) "
                + "JOIN direction_categories dc  ON(dc._id = d.direction_category_id) "
                + "WHERE j._id = ? ";
        Cursor directionCategoryCursor = this.mDb.rawQuery(sql, new String[] {Long.toString(journalId)});
        directionCategoryCursor.moveToFirst();

        return directionCategoryCursor;
    }

    public String getDirectionCategoryNameForJournalEntry(long journalEntryId) {
        String sql = "SELECT dc.name "
            + "FROM direction_categories dc "
            + "JOIN journal_entries je ON (je.direction_category_id = dc._id) "
            + "WHERE je._id = ? ";
        Cursor directionCursor = this.mDb.rawQuery(sql, new String[] {Long.toString(journalEntryId)});
        directionCursor.moveToFirst();

        return directionCursor.getString(directionCursor.getColumnIndexOrThrow(KEY_NAME));
    }
}
