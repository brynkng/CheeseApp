package com.cheeseapp.Activity;

import android.app.Activity;
import android.database.Cursor;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import com.cheeseapp.DbAdapter.CheeseDbAdapter;
import com.cheeseapp.DbAdapter.CheeseTypeDbAdapter;
import com.cheeseapp.R;
import com.cheeseapp.Util.Util;

/**
 * User: Bryan King
 * Date: 4/29/12
 */
public class CheeseInfo extends Activity {

    private CheeseDbAdapter mCheeseDb;
    private CheeseTypeDbAdapter mCheeseTypeDb;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.cheese_info);

        _initializeDatabases();

        long cheeseId = this._getCheeseId(savedInstanceState);
        Cursor CheeseCursor =  this.mCheeseDb.getCheese(cheeseId);
        startManagingCursor(CheeseCursor);

        _setupCheesePicture(CheeseCursor);

        _setupCheeseName(CheeseCursor);

        _setupCheeseTypes(cheeseId);

        _setupCheeseDescription(CheeseCursor);
    }

    private void _initializeDatabases() {
        this.mCheeseDb = new CheeseDbAdapter(this);
        this.mCheeseDb.open();

        this.mCheeseTypeDb = new CheeseTypeDbAdapter(this);
        this.mCheeseTypeDb.open();
    }

    @Override
    protected void onDestroy() {
        this.mCheeseDb.close();
        this.mCheeseTypeDb.close();
        super.onDestroy();
    }

    private void _setupCheeseDescription(Cursor cheeseCursor) {
        TextView CheeseDescription = (TextView) findViewById(R.id.description);
        CheeseDescription.setText(cheeseCursor.getString(cheeseCursor.getColumnIndexOrThrow(CheeseDbAdapter.KEY_DESCRIPTION)));
    }

    private void _setupCheeseName(Cursor cheeseCursor) {
        TextView CheeseName = (TextView) findViewById(R.id.cheeseInfoName);
        CheeseName.setText(cheeseCursor.getString(cheeseCursor.getColumnIndexOrThrow(CheeseDbAdapter.KEY_NAME)));
    }

    private void _setupCheeseTypes(long cheeseId) {
        Cursor TypeCursor = this.mCheeseTypeDb.getCheeseTypesForCheese(cheeseId);
        startManagingCursor(TypeCursor);

        String allCheeseTypes = "";
        TypeCursor.moveToFirst();
        for (int i = 0; i < TypeCursor.getCount(); i++) {
            String cheeseType = TypeCursor.getString(TypeCursor.getColumnIndexOrThrow(CheeseTypeDbAdapter.KEY_TYPE));
            String formattedCheeseType = cheeseType + "\n";
            allCheeseTypes += formattedCheeseType;

            TypeCursor.moveToNext();
        }
        TextView CheeseTypes = (TextView) findViewById(R.id.cheeseTypes);
        CheeseTypes.setText(allCheeseTypes);
    }

    private void _setupCheesePicture(Cursor cheeseCursor) {
        int firstColumn = 1;
        int picResource = Util.getImageResourceFromCursor(this, cheeseCursor, firstColumn);

        ImageView imageView = (ImageView) findViewById(R.id.largeCheeseImg);
        imageView.setImageResource(picResource);
    }

    private long _getCheeseId(Bundle savedInstanceState) {
        String cheeseIdKey = getString(R.string.key_cheese_id);
        Long cheeseId = (savedInstanceState == null) ? null : (Long) savedInstanceState.getSerializable(cheeseIdKey);

        if (cheeseId == null) {
            Bundle extras = getIntent().getExtras();
            cheeseId = extras.getLong(cheeseIdKey);
        }

        return cheeseId;
    }
}