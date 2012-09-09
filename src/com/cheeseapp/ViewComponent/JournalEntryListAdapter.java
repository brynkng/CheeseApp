//package com.cheeseapp.ViewComponent;
//
//import android.content.Context;
//import android.database.Cursor;
//import android.text.Editable;
//import android.text.TextWatcher;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.EditText;
//import android.widget.SimpleCursorAdapter;
//import com.cheeseapp.R;
//
//public class JournalEntryListAdapter extends SimpleCursorAdapter {
//
//    public JournalEntryListAdapter(Context context, int layout, Cursor c, String[] from, int[] to) {
//        super(context, layout, c, from, to);
//    }
//
//    @Override
//    public View getView(int position, View convertView, ViewGroup parent) {
//        EditText editJournal;
//        int tmp_position = position;
//
//        if (convertView == null) {
//
//            LayoutInflater vi = (LayoutInflater)
//            parent.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//            convertView = vi.inflate(R.layout.journal_entry_row, null);
//
//            editJournal = (EditText) convertView.findViewById(R.id.journalEntryText);
//
//            convertView.setTag(editJournal);
//
//        } else {
//            editJournal = (EditText) convertView.getTag();
//        }
//       _initJournalEditTexts(holder.scoreToUpdate);
//       editJournal.setText(scoresToUpdate[tmp_position]);
//       editJournal.addTextChangedListener(new TextWatcher() {
//
//                @Override
//                public void onTextChanged(CharSequence s, int start,
//                        int before, int count) {
////                    scoresToUpdate[tmp_position] = s.toString();
//                }
//
//                @Override
//                public void beforeTextChanged(CharSequence s, int start,
//                        int count, int after) {
//
//                }
//
//                @Override
//                public void afterTextChanged(Editable s) {
//
//                }
//            });
//
//        return super.getView(position, convertView, parent);
//    }
//}
