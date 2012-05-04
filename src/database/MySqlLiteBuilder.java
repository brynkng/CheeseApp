package database;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;

/**
 * User: Bryan King
 * Date: 4/29/12
 */
public class MySqlLiteBuilder extends SQLiteQueryBuilder {

    public Cursor query(SQLiteDatabase db, String[] projectionIn, String selection, String[] selectionArgs) {
        return super.query(db, projectionIn, selection, selectionArgs, null, null, null);
    }
}
