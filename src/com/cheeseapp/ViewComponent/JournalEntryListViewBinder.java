package com.cheeseapp.ViewComponent;

import android.content.Context;
import android.database.Cursor;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import com.cheeseapp.DbAdapter.JournalEntryDbAdapter;
import com.cheeseapp.R;

/**
* User: Bryan King
* Date: 7/11/12
*/
public class JournalEntryListViewBinder implements SimpleCursorAdapter.ViewBinder {

    JournalEntryDbAdapter mJournalEntryDb;
    long mJournalId;

    public JournalEntryListViewBinder(JournalEntryDbAdapter journalEntryDbAdapter, long journalId) {
        mJournalEntryDb = journalEntryDbAdapter;
        mJournalId = journalId;
    }

    @Override
    public boolean setViewValue(View view, Cursor cursor, int columnIndex) {
        int viewId = view.getId();
        if (viewId == R.id.journalEntryCategoryText) {
            String categoryName = cursor.getString(columnIndex);

            TextView currentDirectionCategoryView = (TextView) view;
            currentDirectionCategoryView.setText(categoryName);

        } else if (viewId == R.id.journalEntryText) {
            String entryText = cursor.getString(columnIndex);

            EditText editJournalEntry = (EditText) view;
            editJournalEntry.setText(entryText);

            long categoryId = cursor.getLong(cursor.getColumnIndexOrThrow("category_id"));
            editJournalEntry.setTag(categoryId);

             editJournalEntry.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                public void onFocusChange(View v, boolean hasFocus) {
                    if (!hasFocus){
                        final EditText JournalEntry = (EditText) v;
                        long categoryId = (Long)JournalEntry.getTag();
                        mJournalEntryDb.setJournalEntry(
                                mJournalId,
                                categoryId,
                                JournalEntry.getText().toString()
                        );
                    }
                }
            });

//            editJournalEntry.addTextChangedListener(new TextWatcher() {
//
//                @Override
//                public void onTextChanged(CharSequence s, int start,
//                                          int before, int count) {
//                    scoresToUpdate[tmp_position] = s.toString();
//                }
//
//                @Override
//                public void beforeTextChanged(CharSequence s, int start,
//                                              int count, int after) {
//
//                }
//
//                @Override
//                public void afterTextChanged(Editable s) {
//                    editJournalEntry
//                }
//            });


        }

        return true;
    }
}
