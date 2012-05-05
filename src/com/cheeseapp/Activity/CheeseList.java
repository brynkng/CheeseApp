package com.cheeseapp.Activity;

import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.UserDictionary;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import com.cheeseapp.DbAdapter.CheeseDbAdapter;
import com.cheeseapp.DbAdapter.CheeseTypeDbAdapter;
import com.cheeseapp.R;
import com.cheeseapp.Util.Util;

/**
 * User: Bryan King
 * Date: 4/21/12
 */
public class CheeseList extends ListActivity {

    private CheeseDbAdapter mCheeseDb;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.cheese_list);

        this.mCheeseDb = new CheeseDbAdapter(this);
        this.mCheeseDb.open();
        this.mCheeseDb.prePopulate();

        Cursor cAllCheeses = this.mCheeseDb.getAllCheeses();
        startManagingCursor(cAllCheeses);
        
        String[] from = new String[] {CheeseDbAdapter.KEY_NAME, CheeseDbAdapter.KEY_NAME};
        int[] to = new int[] {R.id.cheeseRowName, R.id.smallCheeseImg};
        SimpleCursorAdapter CheeseListAdapter = new SimpleCursorAdapter(
            this,
            R.layout.cheese_row,
            cAllCheeses,
            from,
            to
        );
        CheeseListAdapter.setViewBinder(CheeseListViewBinder);
        setListAdapter(CheeseListAdapter);

    }

    @Override
    protected void onPause() {
        super.onPause();
        //TODO FILL
    }

    @Override
    protected void onResume() {
        super.onResume();
        //TODO FILL
    }

    @Override
    protected void onListItemClick(ListView listView, View view, int position, long cheeseId) {
        super.onListItemClick(listView, view, position, cheeseId);

        Intent intent = new Intent(this, CheeseInfo.class);
        intent.putExtra(getString(R.string.key_cheese_id), cheeseId);
        startActivity(intent);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        this.mCheeseDb.close();
    }

    private final SimpleCursorAdapter.ViewBinder CheeseListViewBinder = new SimpleCursorAdapter.ViewBinder() {

        @Override
        public boolean setViewValue(View view, Cursor cursor, int columnIndex) {
            int viewId = view.getId();
            switch(viewId) {
                case R.id.cheeseRowName:

                    TextView cheeseNameView = (TextView) view;
                    cheeseNameView.setText(cursor.getString(columnIndex));

                    break;
                case R.id.smallCheeseImg:

                    ImageView cheesePictureView = (ImageView) view;
                    Context context = view.getContext();

                    int cheesePicture = Util.getImageResourceFromCursor(context, cursor, columnIndex);

                    cheesePictureView.setImageResource(cheesePicture);

                    break;
            }

            return true;
        }
    };

}
