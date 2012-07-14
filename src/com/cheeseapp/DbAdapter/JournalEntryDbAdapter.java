package com.cheeseapp.DbAdapter;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

/**
 * User: Bryan King
 * Date: 4/21/12
 */
public class JournalEntryDbAdapter extends DbAdapter{

    //Journal Entries table
    public static final String TABLE = "journal_entries";

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
        this.createJournalEntry(1, 1, "First step go!");
        this.createJournalEntry(1, 2, "Second step go!");
        this.createJournalEntry(1, 3, "Third step go!");

        this.createJournalEntry(2, 1, "First step!");
        this.createJournalEntry(2, 2, "Second step yo!");

        this.createJournalEntry(3, 1, "First step my man!");
    }

    public long createJournalEntry(long journalId, long directionCategoryId, String text) {
        ContentValues initialValues = new ContentValues();
        initialValues.put(KEY_JOURNAL_ID, journalId);
        initialValues.put(KEY_DIRECTION_CATEGORY_ID, directionCategoryId);
        initialValues.put(KEY_TEXT, text);

        return this.mDb.insert(TABLE, null, initialValues);
    }

    /**
     * @return Cursor over all journal entries
     */
    public Cursor getJournalEntries(long journalId) {
        return this.mDb.query(
            TABLE,
            new String[] {KEY_JOURNAL_ID, KEY_DIRECTION_CATEGORY_ID, KEY_TEXT, KEY_LAST_EDITED_DATE},
            "journal_id = " + journalId,
            null,
            null,
            null,
            null,
            null
        );
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
