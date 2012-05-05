package com.cheeseapp.Activity;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.view.*;
import android.widget.*;
import com.cheeseapp.DbAdapter.CheeseDbAdapter;
import com.cheeseapp.DbAdapter.CheeseTypeDbAdapter;
import com.cheeseapp.DbAdapter.NoteDbAdapter;
import com.cheeseapp.R;
import com.cheeseapp.Util.Util;

/**
 * User: Bryan King
 * Date: 4/29/12
 */
public class CheeseInfo extends Activity {

    private CheeseDbAdapter mCheeseDb;
    private CheeseTypeDbAdapter mCheeseTypeDb;
    private NoteDbAdapter mNoteDb;
    private long cheeseId;

    private PopupWindow popup;
    private EditText EditNoteView;
    private static final int DELETE_NOTE_KEY = 1;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.cheese_info);

        _initializeDatabases();

        cheeseId = this._getCheeseId(savedInstanceState);
        Cursor CheeseCursor =  this.mCheeseDb.getCheese(cheeseId);
        startManagingCursor(CheeseCursor);

        _setupCheesePicture(CheeseCursor);

        _setupCheeseName(CheeseCursor);

        _setupCheeseTypes();

        _setupCheeseDescription(CheeseCursor);

        _setupNotes();
    }

    private void _initializeDatabases() {
        this.mCheeseDb = new CheeseDbAdapter(this);
        this.mCheeseDb.open();

        this.mCheeseTypeDb = new CheeseTypeDbAdapter(this);
        this.mCheeseTypeDb.open();

        this.mNoteDb = new NoteDbAdapter(this);
        this.mNoteDb.open();
    }

    @Override
    protected void onDestroy() {
        this.mCheeseDb.close();
        this.mCheeseTypeDb.close();
        this.mNoteDb.close();
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

    private void _setupCheeseTypes() {
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

    private void _setupNotes() {
        _displayNotes();

        final Button AddNoteButton = (Button) findViewById(R.id.addNoteButton);
        AddNoteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                _initiatePopupWindow();
            }
        });
    }

    private void _initiatePopupWindow() {
        try {
            LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            //Inflate the view from a predefined XML layout
            View layout = inflater.inflate(
                R.layout.note_popup,
                (ViewGroup) findViewById(R.id.notePopup)
            );

            Display display = getWindowManager().getDefaultDisplay();
            int popupMargin = 60;
            int width = display.getWidth() - popupMargin;
            int height = display.getHeight() - popupMargin * 2;

            popup = new PopupWindow(layout, width, height, true);
            popup.setAnimationStyle(R.style.Animation_Popup);
            popup.showAtLocation(layout, Gravity.CENTER, 0, 0);

            Button cancelButton = (Button) layout.findViewById(R.id.cancelNotePopupButton);
            cancelButton.setOnClickListener(new View.OnClickListener() {
                public void onClick(View view) {
                    popup.dismiss();
                }
            });

            EditNoteView = (EditText) layout.findViewById(R.id.editNoteText);
            Button saveButton = (Button) layout.findViewById(R.id.saveNoteButton);
            saveButton.setOnClickListener(new View.OnClickListener() {
                public void onClick(View view) {
                    String note = EditNoteView.getText().toString();
                    if (!note.isEmpty()) {
                        mNoteDb.addNote(cheeseId, note);
                    }

                    _displayNotes();
                    popup.dismiss();
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void _displayNotes() {
        Cursor myNotes = this.mNoteDb.getNotesForCheese(cheeseId);
        startManagingCursor(myNotes);

        String[] from = new String[] {NoteDbAdapter.KEY_NOTE};
        int[] to = new int[] {R.id.note_row};
        SimpleCursorAdapter NoteListAdapter = new SimpleCursorAdapter(
                this,
                R.layout.note_row,
                myNotes,
                from,
                to
        );

        ListView NoteListView = (ListView)findViewById(R.id.note_list);
        NoteListView.setAdapter(NoteListAdapter);

        registerForContextMenu(NoteListView);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        menu.add(0, DELETE_NOTE_KEY, 0, R.string.delete_note);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case DELETE_NOTE_KEY:
                AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
                this.mNoteDb.deleteNote(info.id);
                _displayNotes();
                return true;
        }
        return super.onContextItemSelected(item);
    }
}