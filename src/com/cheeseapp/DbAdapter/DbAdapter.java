package com.cheeseapp.DbAdapter;

import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;

/**
 * User: Bryan King
 * Date: 4/21/12
 */
abstract public class DbAdapter {
    //Protected
    protected SQLiteDatabase mDb;

    //Private
    private static final String TAG = "CheeseDbAdapter";
    private DatabaseHelper mDbHelper;
    private Context mCtx;
    private static ArrayList<String> TABLE_CREATES = new ArrayList<String>();

    //Final
    protected static final String DATABASE_NAME = "cheeseApp";
    protected static final int DATABASE_VERSION = 1;

    public DbAdapter(Context context) {
        this.mCtx = context;
        this.setupDbCreates();
    }

    private void setupDbCreates() {
        //cheeses
        String cheeseTable = "CREATE TABLE IF NOT EXISTS " + CheeseDbAdapter.TABLE
                + "("
                + CheeseDbAdapter.KEY_ID + " integer primary key autoincrement,"
                + CheeseDbAdapter.KEY_NAME + " text,"
                + CheeseDbAdapter.KEY_FAVORITE + " tinyint(1) DEFAULT 0,"
                + CheeseDbAdapter.KEY_DESCRIPTION + " text"
                + ");";
        TABLE_CREATES.add(cheeseTable);

        //cheese_types
        String cheeseTypesTable = "CREATE TABLE IF NOT EXISTS " + "cheese_types"
                + "("
                + CheeseTypeDbAdapter.KEY_ID + " integer primary key autoincrement,"
                + CheeseTypeDbAdapter.KEY_TYPE + " text"
                + ");";
        TABLE_CREATES.add(cheeseTypesTable);

        //cheese_types_to_cheese
        String cheeseTypesToCheeseTable = "CREATE TABLE IF NOT EXISTS " + "cheese_types_to_cheese"
                + "("
                + "cheese_id integer,"
                + "cheese_type_id" + " integer"
                + ");";
        TABLE_CREATES.add(cheeseTypesToCheeseTable);

        //notes
        String noteTable = "CREATE TABLE IF NOT EXISTS " + NoteDbAdapter.TABLE
                + "("
                + NoteDbAdapter.KEY_ID + " integer primary key autoincrement,"
                + NoteDbAdapter.KEY_CHEESE_ID + " integer,"
                + NoteDbAdapter.KEY_NOTE + " text"
                + ");";
        TABLE_CREATES.add(noteTable);
    }

    abstract void prePopulate();

    /**
     * Open the cheeses database. If it cannot be opened, try to create a new
     * instance of the database. If it cannot be created, throw an exception to
     * signal the failure
     *
     * @return this (self reference, allowing this to be chained in an
     *         initialization call)
     * @throws android.database.SQLException if the database could be neither opened or created
     */
    public DbAdapter open() throws SQLException {
        this.mDbHelper = new DatabaseHelper(mCtx);
        mDb = this.mDbHelper.getWritableDatabase();
        return this;
    }

    public void close() {
        this.mDbHelper.close();
    }

    private static class DatabaseHelper extends SQLiteOpenHelper {

        DatabaseHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            for (String tableCreate : TABLE_CREATES) {
                db.execSQL(tableCreate);
            }
        }

        @Override
        public void onOpen(SQLiteDatabase db) {
            super.onOpen(db);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            Log.w(TAG, "Upgrading database from version " + oldVersion + " to "
                    + newVersion + ", which will destroy all old data");
            db.execSQL("DROP TABLE IF EXISTS cheeses");
            onCreate(db);
        }
    }
}
