package com.cheeseapp.DbAdapters;

import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * User: Bryan King
 * Date: 4/21/12
 */
abstract public class DbAdapter {
    //Protected
    protected SQLiteDatabase mDb;
    protected static String DATABASE_TABLE;

    //Private
    private static final String TAG = "CheeseDbAdapter";
    private DatabaseHelper mDbHelper;
    private static String DATABASE_CREATE;
    private Context mCtx;

    //Final
    protected static final String DATABASE_NAME = "cheeseApp";
    protected static final int DATABASE_VERSION = 1;

    public DbAdapter(Context context, String dbCreate, String dbTable) {
        DATABASE_CREATE = dbCreate;
        DATABASE_TABLE = dbTable;

        this.mCtx = context;
    }

    
    /**
     * Open the cheeses database. If it cannot be opened, try to create a new
     * instance of the database. If it cannot be created, throw an exception to
     * signal the failure
     *
     * @return this (self reference, allowing this to be chained in an
     *         initialization call)
     * @throws android.database.SQLException if the database could be neither opened or created
     */
    protected DbAdapter open() throws SQLException {
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

            db.execSQL(DATABASE_CREATE);
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
