package com.cheeseapp.Activity;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.widget.*;
import com.actionbarsherlock.app.SherlockActivity;
import com.cheeseapp.DbAdapter.CheeseDbAdapter;
import com.cheeseapp.R;
import com.cheeseapp.Util.Util;

/**
 * User: Bryan King
 * Date: 4/21/12
 */
public class CheeseList extends MyCheeseActivity {

    private CheeseDbAdapter mCheeseDb;
    private static final int FAVORITE_NOTE_KEY = 1;
    private ListView mListView;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.cheese_list);
        mListView =  (ListView)findViewById(R.id.cheeseList);
        mListView.setOnItemClickListener(onListItemClickListener);

        this.mCheeseDb = new CheeseDbAdapter(this);
        this.mCheeseDb.open();
        this.mCheeseDb.prePopulate();
        _setupCheeseList();

        registerForContextMenu(mListView);
    }

    private void _setupCheeseList() {
        Cursor cAllCheeses = this.mCheeseDb.getAllCheeses();
        startManagingCursor(cAllCheeses);

        String[] from = new String[] {CheeseDbAdapter.KEY_NAME, CheeseDbAdapter.KEY_NAME, CheeseDbAdapter.KEY_ID};
        int[] to = new int[] {R.id.cheeseRowName, R.id.smallCheeseImg, R.id.listFavoriteIcon};
        SimpleCursorAdapter CheeseListAdapter = new SimpleCursorAdapter(
                this,
                R.layout.cheese_row,
                cAllCheeses,
                from,
                to
        );
        CheeseListAdapter.setViewBinder(CheeseListViewBinder);
        mListView.setAdapter(CheeseListAdapter);
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

    protected AdapterView.OnItemClickListener onListItemClickListener = new AdapterView.OnItemClickListener() {
        public void onItemClick(android.widget.AdapterView<?> adapterView, android.view.View view, int i, long l) {
            Intent intent = new Intent(adapterView.getContext(), CheeseInfo.class);
            intent.putExtra(getString(R.string.key_cheese_id), l);
            startActivity(intent);
        }
    };

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;
        long cheeseId = info.id;

        int favoriteText;
        if (this.mCheeseDb.isFavorite(cheeseId)) {
            favoriteText = R.string.un_favorite;
        } else {
            favoriteText = R.string.make_favorite;
        }
        menu.add(0, FAVORITE_NOTE_KEY, 0, favoriteText);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case FAVORITE_NOTE_KEY:
            AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
            long cheeseId = info.id;
            this.mCheeseDb.toggleFavorite(cheeseId);
            _setupCheeseList();
            return true;
        }

        return super.onContextItemSelected(item);
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
                
                case R.id.listFavoriteIcon:

                    long cheeseId = cursor.getLong(columnIndex);
                    final boolean isFavorite = mCheeseDb.isFavorite(cheeseId);

                    if (isFavorite) {
                        ImageView favoriteStarIcon = (ImageView) view;
                        favoriteStarIcon.setImageResource(R.drawable.star_on);
                    }
                    break;
            }

            return true;
        }
    };
}
