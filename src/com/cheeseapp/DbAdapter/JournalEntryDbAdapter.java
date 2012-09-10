package com.cheeseapp.DbAdapter;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.text.format.Time;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * User: Bryan King
 * Date: 4/21/12
 */
public class JournalEntryDbAdapter extends DbAdapter{

    //Journal Entries table
    public static final String TABLE = "journal_entries";

    public static final String KEY_ID = "_id";
    public static final String KEY_JOURNAL_ID = "journal_id";
    public static final String KEY_DIRECTION_CATEGORY_ID = "direction_category_id";
    public static final String KEY_TEXT = "text";
    public static final String KEY_LAST_EDITED_DATE = "last_edited_date";

    /**
     * @param ctx the Context within which to work
     */
    public JournalEntryDbAdapter(Context ctx) {
        super(ctx);
    }

    @Override
    public void prePopulate() {
        this.mDb.execSQL("delete from " + TABLE);
        this.mDb.execSQL("delete from sqlite_sequence where name=" + "'" + TABLE + "'");
        this.setJournalEntry(1, 1, "First step go!");
        this.setJournalEntry(1, 2, "Second step go!");
        this.setJournalEntry(1, 3, "Third step go!");

        this.setJournalEntry(2, 1, "First step!");
        this.setJournalEntry(2, 2, "Second step yo!");

        this.setJournalEntry(3, 1, "First step my man!");
    }

    public long setJournalEntry(long journalId, long directionCategoryId, String text) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:ss");
        String currentDateandTime = sdf.format(new Date());
        ContentValues initialValues = new ContentValues();
        initialValues.put(KEY_JOURNAL_ID, journalId);
        initialValues.put(KEY_DIRECTION_CATEGORY_ID, directionCategoryId);
        initialValues.put(KEY_TEXT, text);
        initialValues.put(KEY_LAST_EDITED_DATE, currentDateandTime);

        return this.mDb.replace(TABLE, null, initialValues);
    }

    /**
     * This returns all journal entries including those direction categories that have no text stored for them
     *
     * @return Cursor over all journal entries
     */
    public Cursor getJournalEntries(long journalId) {
        String sql = "SELECT je._id, dc.name as category_name, dc._id as category_id, je.text as entry_text "
                + "FROM journals j "
                + "JOIN recipes r  USING(cheese_id) "
                + "JOIN directions d  ON(r._id = d.recipe_id) "
                + "JOIN direction_categories dc  ON(dc._id = d.direction_category_id) "
                + "LEFT JOIN journal_entries je ON (je.direction_category_id = dc._id AND je.journal_id = j._id) "
                + "WHERE j._id = ? "
                + "GROUP BY dc._id ";
        Cursor cJournalEntries = mDb.rawQuery(sql, new String[] {Long.toString(journalId)});
        cJournalEntries.moveToFirst();

        return cJournalEntries;
    }

    public Cursor getLatestJournalEntry(long journalId) {
        String sql = "SELECT * "
            + "FROM journal_entries "
            + "WHERE journal_id = ? "
            + "ORDER BY last_edited_date DESC "
            + "LIMIT 1";
        Cursor cJournalEntry = mDb.rawQuery(sql, new String[] {Long.toString(journalId)});
        cJournalEntry.moveToFirst();

        return cJournalEntry;
    }
}
