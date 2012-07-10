package com.cheeseapp.Activity;

import android.app.ListActivity;
import android.database.Cursor;
import android.os.Bundle;
import android.widget.*;
//import com.actionbarsherlock.app.SherlockListActivity;
import com.cheeseapp.DbAdapter.GlossaryDbAdapter;
import com.cheeseapp.R;

/**
 * User: Bryan King
 * Date: 4/21/12
 */
public class Glossary extends ListActivity {

    private GlossaryDbAdapter mGlossaryDb;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.glossary_list);

        this.mGlossaryDb = new GlossaryDbAdapter(this);
        this.mGlossaryDb.open();
        this.mGlossaryDb.prePopulate();

        _setupGlossary();
    }

    private void _setupGlossary() {
        Cursor cAllGlossaryEntries = this.mGlossaryDb.getAllGlossaryEntries();
        startManagingCursor(cAllGlossaryEntries);

        String[] from = new String[] {GlossaryDbAdapter.KEY_WORD, GlossaryDbAdapter.KEY_DEFINITION};
        int[] to = new int[] {R.id.glossaryWord, R.id.glossaryDefinition};
        SimpleCursorAdapter CheeseListAdapter = new SimpleCursorAdapter(
                this,
                R.layout.glossary_row,
                cAllGlossaryEntries,
                from,
                to
        );
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
    protected void onDestroy() {
        super.onDestroy();
        this.mGlossaryDb.close();
    }
}