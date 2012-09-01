package com.cheeseapp.Activity;

import android.database.Cursor;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import com.cheeseapp.DbAdapter.JournalDbAdapter;
import com.cheeseapp.DbAdapter.JournalEntryDbAdapter;
import com.cheeseapp.R;

/**
 * User: Bryan King
 * Date: 7/14/12
 */
public class JournalInfo extends MyCheeseActivity{
    private Bundle mSavedInstanceState;

    private long mJournalId;
    private JournalDbAdapter mJournalDb;
    private JournalEntryDbAdapter mJournalEntryDb;
    private ListView mListView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.journal_info);
        mListView = (ListView) findViewById(R.id.journalInfoList);

        mSavedInstanceState = savedInstanceState;

        _setupDatabases();

        mJournalId = _getJournalId(savedInstanceState);

        _setupJournalEntries();
    }

    private void _setupJournalEntries() {
        Cursor cJournalEntries = mJournalEntryDb.getJournalEntries(mJournalId);

        String[] from = new String[] {"category_name", "entry_text"};
        int[] to = new int[] {R.id.journalEntryCategoryText, R.id.journalEntryText};

        SimpleCursorAdapter adapter = new SimpleCursorAdapter(
                this,
                R.layout.journal_entry_row,
                cJournalEntries,
                from,
                to
        );

        mListView.setAdapter(adapter);

    }

    private void _setupDatabases() {
        mJournalDb = new JournalDbAdapter(this);
        mJournalDb.open();

        mJournalEntryDb = new JournalEntryDbAdapter(this);
        mJournalEntryDb.open();
    }

    private long _getJournalId(Bundle savedInstanceState) {
        Long journalId = (savedInstanceState == null) ? null : (Long) savedInstanceState.getSerializable(getString(R.string.key_journal_id));

        if (journalId == null) {
            Bundle extras = getIntent().getExtras();
            journalId = extras.getLong(getString(R.string.key_journal_id));
        }

        return journalId;
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putSerializable(getString(R.string.key_journal_id), mJournalId);
    }


    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mJournalDb.close();
        mJournalEntryDb.close();
    }
}
