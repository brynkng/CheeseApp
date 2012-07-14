package com.cheeseapp.Activity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import com.cheeseapp.DbAdapter.CheeseDbAdapter;
import com.cheeseapp.DbAdapter.DirectionCategoryDbAdapter;
import com.cheeseapp.DbAdapter.JournalDbAdapter;
import com.cheeseapp.DbAdapter.JournalEntryDbAdapter;
import com.cheeseapp.R;
import com.cheeseapp.ViewComponent.JournalListViewBinder;

/**
 * User: Bryan King
 * Date: 7/11/12
 */
public class JournalHome extends MyCheeseActivity {

    private ListView mListView;
    private JournalDbAdapter mJournalDb;
    private CheeseDbAdapter mCheeseDb;
    private JournalEntryDbAdapter mJournalEntryDb;
    private DirectionCategoryDbAdapter mDirectionCategoryDb;

    private static final int DELETE_JOURNAL_KEY = 1;
    private static final int CONFIRM_DELETE_JOURNAL_DIALOG = 1;
    private long mSelectedJournalId;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.journal_list);
        mListView = (ListView)findViewById(R.id.journalList);

        mListView.setOnItemClickListener(onListItemClickListener);
        registerForContextMenu(mListView);

        mJournalDb = new JournalDbAdapter(this);
        mJournalDb.open();
        mJournalDb.prePopulate();

        mJournalEntryDb = new JournalEntryDbAdapter(this);
        mJournalEntryDb.open();
        mJournalEntryDb.prePopulate();

        mDirectionCategoryDb = new DirectionCategoryDbAdapter(this);
        mDirectionCategoryDb.open();
        mDirectionCategoryDb.prePopulate();

        mCheeseDb = new CheeseDbAdapter(this);
        mCheeseDb.open();

        _setupJournalList();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mCheeseDb.close();
        mJournalDb.close();
        mJournalEntryDb.close();
        mDirectionCategoryDb.close();
    }

    private void _setupJournalList() {
        Cursor cAllJournals = this.mJournalDb.getAllJournals();
        startManagingCursor(cAllJournals);

        String[] from = new String[] {"cheese_name", "title", "category_name", "last_edited_date"};
        int[] to = new int[] {R.id.journalSmallCheeseImg, R.id.journalRowName, R.id.journalCurrentDirectionCategory, R.id.journalLatestDate};
        SimpleCursorAdapter JournalAdapter = new SimpleCursorAdapter(
                this,
                R.layout.journal_row,
                cAllJournals,
                from,
                to
        );
        JournalAdapter.setViewBinder(new JournalListViewBinder());
        mListView.setAdapter(JournalAdapter);
    }

    private AdapterView.OnItemClickListener onListItemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
            Intent intent = new Intent(adapterView.getContext(), JournalInfo.class);
            intent.putExtra(getString(R.string.key_cheese_id), l);
            startActivity(intent);
        }
    };

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;

        menu.add(0, DELETE_JOURNAL_KEY, 0, R.string.delete);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {

        switch(item.getItemId()) {
            case DELETE_JOURNAL_KEY:
                final AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();

                mSelectedJournalId = info.id;
                showDialog(CONFIRM_DELETE_JOURNAL_DIALOG);
                return true;
        }

        return super.onContextItemSelected(item);
    }

    @Override
    protected Dialog onCreateDialog(int id) {
        switch (id) {
            case CONFIRM_DELETE_JOURNAL_DIALOG:
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setMessage("Are you sure?")
                        .setCancelable(false)
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {

                                mJournalDb.deleteJournal(mSelectedJournalId);
                                _setupJournalList();
                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });
                return builder.create();
        }

        return null;
    }
}