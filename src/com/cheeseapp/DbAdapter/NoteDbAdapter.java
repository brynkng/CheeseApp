package com.cheeseapp.DbAdapter;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

/**
 * User: Bryan King
 * Date: 4/27/12
 */
public class NoteDbAdapter {

    //Cheese Types table
    public static final String TABLE = "notes";
    public static final String KEY_ID = "_id";
    public static final String KEY_CHEESE_ID = "cheese_id";
    public static final String KEY_NOTE = "note";
    private SQLiteDatabase mDb;

    public NoteDbAdapter(Context ctx) {
        mDb = DbAdapter.getDbInstance(ctx);
    }

    /**
     * @return note id or -1 if failed
     */
    public long addNote(long cheeseId, String note) {
        ContentValues initialValues = new ContentValues();
        initialValues.put(KEY_CHEESE_ID, cheeseId);
        initialValues.put(KEY_NOTE, note);

        return this.mDb.insert(TABLE, null, initialValues);
    }

    public boolean deleteNote(long noteId) {
        return this.mDb.delete(TABLE, KEY_ID + "=" + noteId, null) > 0;
    }

    public Cursor getNotesForCheese(long cheeseId) {
        return this.mDb.query(TABLE, new String[] {TABLE + "." + KEY_ID, KEY_NOTE}, "cheese_id = ?", new String[] {Long.toString(cheeseId)}, null, null, null);
    }
}
