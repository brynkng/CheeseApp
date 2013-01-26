package com.cheeseapp.DbAdapter;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

/**
 * User: Bryan King
 * Date: 4/21/12
 */
public class JournalDbAdapter {

    //Journals table
    public static final String TABLE = "journals";

    public static final String KEY_ID = "_id";
    public static final String KEY_TITLE = "title";
    public static final String KEY_START_DATE = "start_date";
    public static final String KEY_CHEESE_ID = "cheese_id";
    private SQLiteDatabase mDb;

    /**
     * @param ctx the Context within which to work
     */
    public JournalDbAdapter(Context ctx) {
        mDb = DbAdapter.getDbInstance(ctx);
    }

    /**
     * Create a new cheese. Make sure the picture file has the exact same name as the one provided here.
     *
     * @param cheeseId
     * @param title
     * @return journalId or -1 if failed
     */
    public long createJournal(long cheeseId, String title) {
        ContentValues initialValues = new ContentValues();
        initialValues.put(KEY_CHEESE_ID, cheeseId);
        initialValues.put(KEY_TITLE, title);

        return this.mDb.insert(TABLE, null, initialValues);
    }

    public long createJournal(long cheeseId) {
        return createJournal(cheeseId, "");
    }

    /**
     * @param journalId
     * @return true if deleted, false otherwise
     */
    public boolean deleteJournal(long journalId) {
        return this.mDb.delete(TABLE, KEY_ID + "=" + journalId, null) > 0;
    }

    /**
     * @return Cursor over all journals
     */
    public Cursor getAllJournals() {
        String sql =
            "SELECT j._id, "
                + "c.name AS cheese_name, "
                + "dc.name AS category_name, "
                + "DATE(MAX(je.last_edited_date)) AS last_edited_date, "
                + "CASE WHEN j.title != '' THEN j.title ELSE c.name END AS title "
            + "FROM journals j "
            + "JOIN cheeses c ON(j.cheese_id = c._id) "
            + "JOIN journal_entries je ON(je.journal_id = j._id) "
            + "JOIN direction_categories dc ON(je.direction_category_id = dc._id) "
            + "GROUP BY j._id "
            + "ORDER BY last_edited_date";
        Cursor cJournalCursor = mDb.rawQuery(sql, new String[] {});
        cJournalCursor.moveToFirst();

        return cJournalCursor;
    }

    public Cursor getAllJournalsWithCheese(long cheeseId) {
        String sql =
            "SELECT j._id, "
                + "DATE(MAX(je.last_edited_date)) AS last_edited_date, "
                + "CASE WHEN j.title != '' THEN j.title ELSE c.name END AS title "
            + "FROM journals j "
            + "JOIN cheeses c ON(j.cheese_id = c._id) "
            + "JOIN journal_entries je ON(je.journal_id = j._id) "
            + "WHERE c._id = ? "
            + "GROUP BY j._id "
            + "ORDER BY last_edited_date";
        Cursor cJournalCursor = mDb.rawQuery(sql, new String[] {Long.toString(cheeseId)});
        cJournalCursor.moveToFirst();

        return cJournalCursor;
    }

    /**
     *
     * @param journalId
     * @return Cursor
     * @throws android.database.SQLException
     */
    public Cursor getJournal(long journalId) throws SQLException {
        Cursor mCursor =
                this.mDb.query(true, TABLE, this._getJournalFields(), KEY_ID + "=" + journalId, null,
                        null, null, null, null);
        if (mCursor != null) {
            mCursor.moveToFirst();
        }
        return mCursor;

    }

    private String[] _getJournalFields() {
        return new String[] {KEY_ID, KEY_TITLE, KEY_START_DATE, KEY_CHEESE_ID};
    }
}
