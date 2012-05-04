package com.cheeseapp.DbAdapter;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import database.MySqlLiteBuilder;

/**
 * User: Bryan King
 * Date: 4/27/12
 */
public class NoteDbAdapter extends DbAdapter{

    //Cheese Types table
    public static final String TABLE = "notes";
    public static final String KEY_ID = "note_id";
    public static final String KEY_CHEESE_ID = "cheese_id";
    public static final String KEY_NOTE = "note";

    @Override
    public void prePopulate() {
        this.mDb.execSQL("delete from " + TABLE);
        this.addNote(1, "Hey this cheese is awesome!");
        this.addNote(1, "Make this shit again!");
        this.addNote(2, "Make this shit again!");
    }

    public NoteDbAdapter(Context ctx) {
        super(ctx);
    }

    /**
     * @return note id or -1 if failed
     */
    public long addNote(long cheeseId, String note) {
        ContentValues initialValues = new ContentValues();
        initialValues.put(CheeseDbAdapter.KEY_ID, cheeseId);
        initialValues.put(KEY_NOTE, note);

        return this.mDb.insert(TABLE, null, initialValues);
    }

    public boolean deleteNote(long noteId) {
        return this.mDb.delete(TABLE, KEY_ID + "=" + noteId, null) > 0;
    }

    public Cursor getNotes(long cheeseId) {
        return this.mDb.query(TABLE, new String[] {KEY_ID, KEY_NOTE}, null, null, null, null, null);
    }
    
    public Cursor getNotesForCheese(long cheeseId) {
        MySqlLiteBuilder sqlBuilder = new MySqlLiteBuilder();
        sqlBuilder.setTables(TABLE + " JOIN notes_to_cheese ON (cheeses._id)");

        return this.mDb.query(TABLE, new String[] {KEY_ID, KEY_NOTE}, "WHERE ", null, null, null, null);

    }
}
