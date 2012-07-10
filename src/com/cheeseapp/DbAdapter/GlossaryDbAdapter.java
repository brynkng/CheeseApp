package com.cheeseapp.DbAdapter;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

/**
 * User: Bryan King
 * Date: 4/27/12
 */
public class GlossaryDbAdapter extends DbAdapter{

    //Cheese Types table
    public static final String TABLE = "glossary_entries";
    public static final String KEY_ID = "_id";
    public static final String KEY_WORD = "word";
    public static final String KEY_DEFINITION = "definition";

    @Override
    public void prePopulate() {
        this.mDb.execSQL("delete from " + TABLE);
        this.addGlossaryEntry("Cheese", "It's made from milk!");
        this.addGlossaryEntry("Rennet", "Enzymes mofo!");
        this.addGlossaryEntry("Cheese Press", "Press your shit!");
    }

    public GlossaryDbAdapter(Context ctx) {
        super(ctx);
    }

    /**
     * @param word
     * @param definition
     * @return Glossary entry id or -1 if failed
     */
    public long addGlossaryEntry(String word, String definition) {
        ContentValues initialValues = new ContentValues();
        initialValues.put(KEY_WORD, word);
        initialValues.put(KEY_DEFINITION, definition);

        return this.mDb.insert(TABLE, null, initialValues);
    }

    public boolean deleteGlossaryEntry(long glossaryEntryId) {
        return this.mDb.delete(TABLE, KEY_ID + "=" + glossaryEntryId, null) > 0;
    }

    public Cursor getAllGlossaryEntries() {
        return this.mDb.query(TABLE, new String[] {KEY_ID, KEY_WORD, KEY_DEFINITION}, null, null, null, null, null);
    }
}
