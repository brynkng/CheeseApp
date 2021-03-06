package com.cheeseapp.DbAdapter;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * User: Bryan King
 * Date: 4/21/12
 */
public class DbAdapter extends SQLiteOpenHelper {
    //Protected
    protected static SQLiteDatabase mDb;

    private static String DB_PATH = "/data/data/com.cheeseapp/databases/";
    private static String DB_NAME = "cheeseApp.db";
    private final Context myContext;

    private static DbAdapter mDbAdapterInstance;

    private DbAdapter(Context context){
        super(context, DB_NAME, null, 1);
        this.myContext = context;
        try {
            this.createDataBase();
        } catch (IOException E) {
            throw new Error("Could not open database");
        }
    }

    public static SQLiteDatabase getDbInstance(Context context) {
        if (mDbAdapterInstance == null || mDb == null || !mDb.isOpen()) {
            if (mDbAdapterInstance == null) {
                mDbAdapterInstance = new DbAdapter(context);
            }
            mDb = mDbAdapterInstance.open();
        }

        return mDb;
    }

    /**
     * Creates a empty database on the system and rewrites it with your own database.
     * */
    public void createDataBase() throws IOException {

    	boolean dbExist = checkDataBase();

    	if(dbExist){
    		//do nothing - database already exists
    	}else{

    		//By calling this method and empty database will be created into the default system path
               //of your application so we are gonna be able to overwrite that database with our database.
        	this.getWritableDatabase();

        	try {

    			copyDataBase();

    		} catch (IOException e) {

        		throw new Error("Error copying database");

        	}
    	}

    }

    /**
     * Check if the database already exist to avoid re-copying the file each time you open the application.
     * @return true if it exists, false if it doesn't
     */
    private boolean checkDataBase(){

    	SQLiteDatabase checkDB = null;

    	try{
    		String myPath = DB_PATH + DB_NAME;
    		checkDB = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READWRITE);

    	}catch(SQLiteException e){

    		//database does't exist yet.

    	}

    	if(checkDB != null){

    		checkDB.close();

    	}

    	return checkDB != null ? true : false;
    }

    /**
     * Copies your database from your local assets-folder to the just created empty database in the
     * system folder, from where it can be accessed and handled.
     * This is done by transfering bytestream.
     * */
    private void copyDataBase() throws IOException{

    	//Open your local db as the input stream
    	InputStream myInput = myContext.getAssets().open(DB_NAME);

    	// Path to the just created empty db
    	String outFileName = DB_PATH + DB_NAME;

    	//Open the empty db as the output stream
    	OutputStream myOutput = new FileOutputStream(outFileName);

    	//transfer bytes from the inputfile to the outputfile
    	byte[] buffer = new byte[1024];
    	int length;
    	while ((length = myInput.read(buffer))>0){
    		myOutput.write(buffer, 0, length);
    	}

    	//Close the streams
    	myOutput.flush();
    	myOutput.close();
    	myInput.close();

    }

    public SQLiteDatabase open() {
        String myPath = DB_PATH + DB_NAME;
    	return SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READWRITE);
    }

    @Override
	public synchronized void close() {

    	    if(mDb != null && mDb.isOpen())
    		    mDb.close();

    	    super.close();

	}

	@Override
	public void onCreate(SQLiteDatabase db) {

	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

	}

}
