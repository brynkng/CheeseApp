package com.cheeseapp.Activity;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Point;
import android.graphics.Rect;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.*;
import com.cheeseapp.DbAdapter.JournalDbAdapter;
import com.cheeseapp.DbAdapter.JournalEntryDbAdapter;
import com.cheeseapp.R;

import java.util.HashMap;
import java.util.IllegalFormatCodePointException;

/**
 * User: Bryan King
 * Date: 7/14/12
 */
public class JournalInfo extends MyCheeseActivity{
    public static final String DIRECTION_POSITION_KEY = "direction_position_key";

    private Bundle mSavedInstanceState;

    private long mJournalId;
    private JournalDbAdapter mJournalDb;
    private JournalEntryDbAdapter mJournalEntryDb;
    private LinearLayout mJournalEntryLayout;
    private HashMap<Long, String> mJournalEntries = new HashMap<Long, String>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.journal_info);
        mJournalEntryLayout = (LinearLayout) findViewById(R.id.journalEntryLayout);

        mSavedInstanceState = savedInstanceState;

        _setupDatabases();

        mJournalId = _getJournalId(savedInstanceState);
        _setupJournalEntries();

        Cursor cJournalEntry = mJournalEntryDb.getLatestJournalEntry(mJournalId);
        final int directionCategoryId;

        if (!cJournalEntry.isAfterLast()) {
            directionCategoryId = cJournalEntry.getInt(
                    cJournalEntry.getColumnIndexOrThrow(JournalEntryDbAdapter.KEY_DIRECTION_CATEGORY_ID)
            );
        } else {
            directionCategoryId = 1;
        }

        final ScrollView scrollView = (ScrollView)findViewById(R.id.journalInfoScrollView);
        scrollView.post(new Runnable() {
            @Override
            public void run() {
                int scrollY = (directionCategoryId == 1 ? 1 : directionCategoryId * 125);
                scrollView.scrollTo(0, scrollY);
            }
        });
    }

    private void _setupJournalEntries() {
        Cursor cJournalEntries = mJournalEntryDb.getJournalEntries(mJournalId);
        startManagingCursor(cJournalEntries);

        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        while (!cJournalEntries.isAfterLast()) {
            LinearLayout journalRowLayout = (LinearLayout)inflater.inflate(R.layout.journal_entry_row, null);
            TextView categoryTextView = (TextView)journalRowLayout.findViewById(R.id.journalEntryCategoryText);
            EditText entryEditText = (EditText)journalRowLayout.findViewById(R.id.journalEntryText);
            long categoryId = cJournalEntries.getLong(cJournalEntries.getColumnIndexOrThrow("category_id"));
            entryEditText.setTag(categoryId);

            String categoryText = cJournalEntries.getString(cJournalEntries.getColumnIndexOrThrow("category_name"));
            String entryText = cJournalEntries.getString(cJournalEntries.getColumnIndexOrThrow("entry_text"));


            categoryTextView.setText(categoryText);
            entryEditText.setText(entryText);

            mJournalEntryLayout.addView(journalRowLayout);

            cJournalEntries.moveToNext();
            if (entryText == null) {
                entryText = "";
            }
            mJournalEntries.put(categoryId, entryText);
        }
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
        for (int i = 0; i < mJournalEntryLayout.getChildCount(); i++) {
            LinearLayout entryRow = (LinearLayout)mJournalEntryLayout.getChildAt(i);
            EditText entryEditText = (EditText)entryRow.findViewById(R.id.journalEntryText);
            long categoryId = (Long)entryEditText.getTag();
            String entryText = entryEditText.getText().toString();

            if (!mJournalEntries.get(categoryId).equals(entryText)) {
                mJournalEntryDb.setJournalEntry(mJournalId, categoryId, entryText);
            }
        }

        Toast.makeText(getApplicationContext(), "Saved", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mJournalDb.close();
        mJournalEntryDb.close();
    }
}
