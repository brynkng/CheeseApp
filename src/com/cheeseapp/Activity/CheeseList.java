package com.cheeseapp.Activity;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.widget.*;
import com.cheeseapp.DbAdapter.*;
import com.cheeseapp.R;
import com.cheeseapp.Util.Util;

/**
 * User: Bryan King
 * Date: 4/21/12
 */
public class CheeseList extends MyCheeseActivityWithTabs {

    private CheeseDbAdapter mCheeseDb;
    private static final int FAVORITE_NOTE_KEY = 1;
    private ListView mListView;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.cheese_list);

        getSupportActionBar().setDisplayHomeAsUpEnabled(false);

        mListView =  (ListView)findViewById(R.id.cheeseList);
        mListView.setOnItemClickListener(onListItemClickListener);

        this.mCheeseDb = new CheeseDbAdapter(this);
        this.mCheeseDb.open();

        //TODO REMOVE
        _prepopulateDbs();

        _setupCheeseList();

        registerForContextMenu(mListView);
    }

    private void _prepopulateDbs() {
        if (mCheeseDb.getAllCheeses().getCount() < 2) {

            mCheeseDb.prePopulate();

            CheeseTypeDbAdapter cheeseTypeDb = new CheeseTypeDbAdapter(this);
            cheeseTypeDb.open();
            cheeseTypeDb.prePopulate();
            cheeseTypeDb.close();

            DirectionCategoryDbAdapter dcAdapter = new DirectionCategoryDbAdapter(this);
            dcAdapter.open();
            dcAdapter.prePopulate();
            dcAdapter.close();

            DirectionDbAdapter dAdapter = new DirectionDbAdapter(this);
            dAdapter.open();
            dAdapter.prePopulate();
            dAdapter.close();

            GlossaryDbAdapter gAdapter = new GlossaryDbAdapter(this);
            gAdapter.open();
            gAdapter.prePopulate();
            gAdapter.close();

            IngredientDbAdapter iAdapter = new IngredientDbAdapter(this);
            iAdapter.open();
            iAdapter.prePopulate();
            iAdapter.close();

            JournalDbAdapter jAdapter = new JournalDbAdapter(this);
            jAdapter.open();
            jAdapter.prePopulate();
            jAdapter.close();

            JournalEntryDbAdapter jeAdapter = new JournalEntryDbAdapter(this);
            jeAdapter.open();
            jeAdapter.prePopulate();
            jeAdapter.close();

            NoteDbAdapter nAdapter = new NoteDbAdapter(this);
            nAdapter.open();
            nAdapter.prePopulate();
            nAdapter.close();

            RecipeDbAdapter rAdapter = new RecipeDbAdapter(this);
            rAdapter.open();
            rAdapter.prePopulate();
            rAdapter.close();
        }
    }

    private void _setupCheeseList() {
        Cursor cAllCheeses = this.mCheeseDb.getAllCheeses();
        startManagingCursor(cAllCheeses);

        String[] from = new String[] {CheeseDbAdapter.KEY_NAME, CheeseDbAdapter.KEY_NAME, CheeseDbAdapter.KEY_ID};
        int[] to = new int[] {R.id.cheeseRowName, R.id.cheeseListSmallCheeseImg, R.id.listFavoriteIcon};
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
            overridePendingTransition(R.anim.fadein, R.anim.fadeout);
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
                
                case R.id.cheeseListSmallCheeseImg:

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
