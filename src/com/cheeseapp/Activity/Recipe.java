package com.cheeseapp.Activity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.*;
import android.widget.*;
import com.actionbarsherlock.view.MenuItem;
import com.cheeseapp.*;
import com.cheeseapp.Curl.CurlPage;
import com.cheeseapp.Curl.CurlView;
import com.cheeseapp.DbAdapter.*;
import net.everythingandroid.timer.TimerActivity;

import java.util.*;

import static java.lang.Math.abs;

/**
 * User: Bryan King
 * Date: 5/6/12
 */
public class Recipe extends MyCheeseActivity {

    private static final int START_JOURNAL_KEY = 0;
    private static final int DIALOG_EDIT_JOURNAL = 0;
    private static final int DIALOG_MULTIPLE_ALARMS = 1;

    private long mCheeseId;
    private RecipeDbAdapter mRecipeDb;
    private CheeseDbAdapter mCheeseDb;
    private JournalDbAdapter mJournalDb;
    private DirectionDbAdapter mDirectionDb;
    private DirectionCategoryDbAdapter mDirectionCategoryDb;
    private Cursor mRecipeCursor;
    private Cursor mDirectionCursor;

    private long mRecipeId;
    private Long mJournalId;
    private ArrayList<RecipePage> mRecipePageList;
    private ArrayList<CategoryGroupedDirection> mCategoryGroupedDirections;
    private CurlView mCurlView;

    private boolean mShowTimerIcon = false;
    private ArrayList<TimerValue> mCurrentTimerValues;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        _initializeDatabases();
        setContentView(R.layout.recipe);

        int index = 0;
        if (getLastNonConfigurationInstance() != null) {
            index = (Integer) getLastNonConfigurationInstance();
        }

        mCheeseId = _getCheeseId(savedInstanceState);
        Cursor cCheese = mCheeseDb.getCheese(mCheeseId);
        String cheeseName = cCheese.getString(cCheese.getColumnIndexOrThrow(CheeseDbAdapter.KEY_NAME));

        getSupportActionBar().setTitle(cheeseName);

        mRecipeId = _getRecipeId(savedInstanceState);
        mJournalId = _tryToGetJournalId(savedInstanceState);
        mCategoryGroupedDirections = _getCategoryGroupedDirections(savedInstanceState);

        mCurlView = (CurlView) findViewById(R.id.recipeCurlView);
        mCurlView.setCurrentIndex(index);
        mCurlView.setPageProvider(new PageProvider());
        mCurlView.setSizeChangedObserver(new SizeChangedObserver());
        mCurlView.setBackgroundColor(Color.WHITE);
        mCurlView.setAllowLastPageCurl(false);


    }

    private Long _tryToGetJournalId(Bundle savedInstanceState) {
        return (savedInstanceState == null) ? null : (Long) savedInstanceState.getSerializable(getString(R.string.key_journal_id));
    }

    public boolean onCreateOptionsMenu(com.actionbarsherlock.view.Menu menu) {
        if (mShowTimerIcon) {
            MenuItem timer = menu.add("Set timer");
            timer.setIcon(R.drawable.device_access_alarms)
                    .setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);

            timer.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {

                    if (mCurrentTimerValues.size() > 1) {
                        showDialog(DIALOG_MULTIPLE_ALARMS);
                    } else {
                        Intent intent = new Intent(Recipe.this, TimerActivity.class);
                        Integer time = mCurrentTimerValues.get(0).getTime();
                        intent.putExtra(mCurrentTimerValues.get(0).getUnit(), time.toString());
                        startActivityForResult(intent, 1);
                    }
                    return true;
                }
            });
        }

        MenuItem journalEdit = menu.add("Edit Journal");
        journalEdit.setIcon(R.drawable.content_edit)
            .setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);

        journalEdit.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {
                if (mJournalId == null) {
                    showDialog(DIALOG_EDIT_JOURNAL);
                } else {
                    Intent intent = new Intent(Recipe.this, JournalInfo.class);
                    intent.putExtra(getString(R.string.key_journal_id), mJournalId);
                    startActivity(intent);
                    overridePendingTransition(R.anim.fadein, R.anim.fadeout);
                }
                return true;
            }
        });

        return super.onCreateOptionsMenu(menu);
    }

    protected Dialog onCreateDialog(int id) {
        Dialog dialog;
        switch(id) {
        case DIALOG_EDIT_JOURNAL:
            dialog = _createEditJournalDialog();
            break;
        case DIALOG_MULTIPLE_ALARMS:
            dialog = _createMultipleAlarmsDialog();
            break;
        default:
            dialog = null;
        }

        return dialog;
    }

    private Dialog _createEditJournalDialog() {
        Dialog dialog;
        Cursor cAllJournals = mJournalDb.getAllJournalsWithCheese(mCheeseId);
        startManagingCursor(cAllJournals);

        final HashMap<Long, String> journals = new HashMap<Long, String>();
        journals.put((long)DIALOG_EDIT_JOURNAL, "Create new journal");
        while (!cAllJournals.isAfterLast()) {
            long journalId = cAllJournals.getLong(cAllJournals.getColumnIndexOrThrow(JournalDbAdapter.KEY_ID));
            String date = cAllJournals.getString(cAllJournals.getColumnIndexOrThrow("last_edited_date"));
            String title = cAllJournals.getString(cAllJournals.getColumnIndexOrThrow("title"));

            journals.put(journalId, title + " - " + date);

            cAllJournals.moveToNext();
        }

        String[] items = journals.values().toArray(new String[journals.size()]);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Select Journal");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int item) {
                long journalId;
                switch (item) {
                    case DIALOG_EDIT_JOURNAL:
                        journalId = mJournalDb.createJournal(mCheeseId);
                        break;
                    default:
                        journalId = (Long) journals.keySet().toArray()[item];
                }

                mJournalId = journalId;
                Intent intent = new Intent(Recipe.this, JournalInfo.class);
                intent.putExtra(getString(R.string.key_journal_id), journalId);
                startActivity(intent);
                overridePendingTransition(R.anim.fadein, R.anim.fadeout);
                removeDialog(DIALOG_EDIT_JOURNAL); //To circumvent dialog caching
            }
        });
        dialog = builder.create();
        return dialog;
    }

    private Dialog _createMultipleAlarmsDialog() {
        Dialog dialog;
        final ArrayList<String> times = new ArrayList<String>();
        for (TimerValue timerValue : mCurrentTimerValues) {

            int time = timerValue.getTime();
            String pluralizedUnit;
            if (time > 1) {
                pluralizedUnit = "s";
            } else {
                pluralizedUnit = "";
            }
            times.add(time + " " + timerValue.getUnit() + pluralizedUnit);
        }

        String[] items = times.toArray(new String[times.size()]);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Select Alarm Timer");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int item) {
                TimerValue timerValue = mCurrentTimerValues.get(item);
                Intent intent = new Intent(Recipe.this, TimerActivity.class);
                Integer time = timerValue.getTime();
                intent.putExtra(timerValue.getUnit(), time.toString());
                startActivityForResult(intent, 1);
                removeDialog(DIALOG_MULTIPLE_ALARMS); //To circumvent dialog caching
            }
        });
        dialog = builder.create();
        return dialog;
    }

    private ArrayList<CategoryGroupedDirection> _getCategoryGroupedDirections(Bundle savedInstanceState) {
        @SuppressWarnings("unchecked")
        ArrayList<CategoryGroupedDirection> categoryGroupedDirections = (savedInstanceState == null) ? null :  (ArrayList<CategoryGroupedDirection>) savedInstanceState.getSerializable("paged_directions");

        if (categoryGroupedDirections == null) {
            categoryGroupedDirections = new ArrayList<CategoryGroupedDirection>();
            Cursor directionC = _getDirectionCursor();

            String directions = "";
            int currentDirectionCategory = 0;
            int directionNum = 1;

            while (!directionC.isAfterLast()) {
                Integer directionCategoryId = directionC.getInt(directionC.getColumnIndexOrThrow(DirectionDbAdapter.KEY_DIRECTION_CATEGORY_ID));
                String directionText = directionC.getString(directionC.getColumnIndexOrThrow(DirectionDbAdapter.KEY_DIRECTION));
                directionText = directionNum + ". " + directionText;
                directionText += "\n\n";

                if(directionC.isFirst()){
                    currentDirectionCategory = directionCategoryId;
                } else if (directionCategoryId != currentDirectionCategory) {
                    categoryGroupedDirections.add(new CategoryGroupedDirection(currentDirectionCategory, directions));
                    currentDirectionCategory = directionCategoryId;
                    directions = "";
                }

                directionC.moveToNext();

                if (directionC.isAfterLast()) {
                    categoryGroupedDirections.add(new CategoryGroupedDirection(currentDirectionCategory, directionText));
                }

                directions += directionText;
                directionNum++;
            }
        }

        return categoryGroupedDirections;
    }

    @Override
    protected void onPause() {
        super.onPause();
        mCurlView.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mCurlView.onResume();
    }

    private long _getRecipeId(Bundle savedInstanceState) {
        Long recipeId = (savedInstanceState == null) ? null :  (Long) savedInstanceState.getSerializable("recipe_id");

        if (recipeId == null) {
            Cursor RecipeCursor = _getRecipeCursor();
            recipeId = RecipeCursor.getLong(RecipeCursor.getColumnIndexOrThrow(RecipeDbAdapter.KEY_ID));
        }

        return recipeId;
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putLong("cheese_id", mCheeseId);
        outState.putLong("recipe_id", mRecipeId);
        if (mJournalId != null) {
            outState.putLong(getString(R.string.key_journal_id), mJournalId);
        }
    }

    private ArrayList<RecipePage> _getRecipePageList(int width, int height) {
        ArrayList<RecipePage> recipeViewList = new ArrayList<RecipePage>();

        LayoutInflater inflater = (LayoutInflater)this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View emptyRecipeLayout = inflater.inflate(R.layout.recipe_page, (ViewGroup) findViewById(R.id.recipePageLayout));
        TextView emptyRecipeText = (TextView) emptyRecipeLayout.findViewById(R.id.recipePageDirectionsText);
        TextView emptyCategoryText = (TextView) emptyRecipeLayout.findViewById(R.id.recipeDirectionCategory);

        Paint recipeTextPaint = emptyRecipeText.getPaint();
        Paint categoryTextPaint = emptyCategoryText.getPaint();
        float fontHeight = abs(recipeTextPaint.getFontMetrics().top) + abs(recipeTextPaint.getFontMetrics().bottom);
        float headerHeight = abs(categoryTextPaint.getFontMetrics().top) + abs(categoryTextPaint.getFontMetrics().bottom);
        headerHeight *= 3;
        int maxLines = (int) abs((height - headerHeight) / fontHeight);
        int maxNumCharsPerLine = (int) (width / (recipeTextPaint.getTextSize() / 2));

        for (CategoryGroupedDirection RecipeDirection : mCategoryGroupedDirections) {
            String directions = RecipeDirection.getDirections();
            int directionCategoryId = RecipeDirection.getDirectionCategoryId();
            String categoryName = mDirectionCategoryDb.getDirectionCategoryName(directionCategoryId);

            while (directions.length() > 0) {
                View recipeLayout = inflater.inflate(R.layout.recipe_page, (ViewGroup) findViewById(R.id.recipePageLayout));

                TextView directionCategoryText = (TextView) recipeLayout.findViewById(R.id.recipeDirectionCategory);
                directionCategoryText.setText(categoryName);

                TextView recipeText = (TextView) recipeLayout.findViewById(R.id.recipePageDirectionsText);
                String finalPageText = "";

                int currentLineNum = 1;
                while (currentLineNum <= maxLines) {
                    int numCharsOnLine = (maxNumCharsPerLine < directions.length()) ? maxNumCharsPerLine : directions.length();
                    String subText = TextUtils.substring(directions, 0, numCharsOnLine);

                    //if this is the last line, clear out the directions
                    //otherwise remove the portion we added
                    if (numCharsOnLine == directions.length()) {
                        directions = "";
                    } else {
                        directions = TextUtils.substring(directions, numCharsOnLine, directions.length());
                    }

                    finalPageText += subText;

                    currentLineNum++;

                    //Don't let it break in the middle of a word
                    if (currentLineNum > maxLines && directions.length() > 0 && directions.charAt(0) != ' ') {
                        int lastIndex = finalPageText.lastIndexOf(' ');
                        String lastSubText = finalPageText.substring(lastIndex + 1, finalPageText.length()); // +1 is to skip the space
                        directions = lastSubText + directions;
                        finalPageText = finalPageText.substring(0, lastIndex);
                    }
                }

                TimerValueRetriever timerValueRetriever = new TimerValueRetriever();
                ArrayList<TimerValue> timerValues = timerValueRetriever.retrieve(finalPageText);
                RecipePage recipePage = new RecipePage(directionCategoryId, recipeLayout, timerValues);

                recipeText.setText(finalPageText);
                recipeViewList.add(recipePage);
            }
        }

        return recipeViewList;
    }

    private Cursor _getRecipeCursor() {
        if (mRecipeCursor == null) {
            mRecipeCursor = mRecipeDb.getRecipeForCheese(mCheeseId);
        }
        
        return mRecipeCursor;
    }

    private Cursor _getDirectionCursor() {
        if (mDirectionCursor == null) {
            mDirectionCursor = mDirectionDb.getDirectionsForRecipe(mRecipeId);
        }

        return mDirectionCursor;
    }

    private void _initializeDatabases() {
        mRecipeDb = new RecipeDbAdapter(this);
        mRecipeDb.open();

        mDirectionDb = new DirectionDbAdapter(this);
        mDirectionDb.open();

        mJournalDb = new JournalDbAdapter(this);
        mJournalDb.open();

        mCheeseDb = new CheeseDbAdapter(this);
        mCheeseDb.open();

        mDirectionCategoryDb = new DirectionCategoryDbAdapter(this);
        mDirectionCategoryDb.open();
    }

    @Override
    protected void onDestroy() {
        mRecipeDb.close();
        mDirectionDb.close();
        mCheeseDb.close();
        mJournalDb.close();
        mDirectionCategoryDb.close();

        super.onDestroy();
    }

    private long _getCheeseId(Bundle savedInstanceState) {
        Long cheeseId = (savedInstanceState == null) ? null : (Long) savedInstanceState.getSerializable(CheeseInfo.CHEESE_ID_KEY);

        if (cheeseId == null) {
            Bundle extras = getIntent().getExtras();
            cheeseId = extras.getLong(CheeseInfo.CHEESE_ID_KEY);
        }

        return cheeseId;
    }

    /**
     * Bitmap provider.
     */
    private class PageProvider implements CurlView.PageProvider {

        @Override
        public int getPageCount() {
            return mRecipePageList.size();
        }

        private Bitmap loadBitmap(int width, int height, int index) {
            Bitmap b = Bitmap.createBitmap(width, height,Bitmap.Config.ARGB_8888);
            Canvas c = new Canvas(b);
            LinearLayout recipeView = (LinearLayout) mRecipePageList.get(index).getRecipeLayout();
            recipeView.measure(
                    View.MeasureSpec.makeMeasureSpec(width, View.MeasureSpec.EXACTLY),
                    View.MeasureSpec.makeMeasureSpec(height, View.MeasureSpec.EXACTLY));
            recipeView.layout(0, 0, recipeView.getMeasuredWidth(), recipeView.getMeasuredHeight());

            recipeView.draw(c);
            return b;
        }

        public void onPageTurn(final int index) {
            runOnUiThread(new Runnable() {
                public void run() {
                    ArrayList<TimerValue> timerValues = mRecipePageList.get(index).getTimerValues();
                    if (timerValues.isEmpty()) {
                        mShowTimerIcon = false;
                    } else {
                        mShowTimerIcon = true;
                        mCurrentTimerValues = timerValues;
                    }
                    Recipe.this.invalidateOptionsMenu();
                }
            });
        }

        @Override
        public void updatePage(CurlPage page, int width, int height, final int index) {

            Bitmap front = loadBitmap(width, height, index);
            page.setTexture(front, CurlPage.SIDE_FRONT);
            page.setColor(Color.WHITE, CurlPage.SIDE_BACK);


//            switch (index) {
//                // First case is image on front side, solid colored back.
//                case 0: {
//                    Bitmap front = loadBitmap(width, height, 0);
//                    page.setTexture(front, CurlPage.SIDE_FRONT);
//                    page.setColor(Color.rgb(180, 180, 180), CurlPage.SIDE_BACK);
//                    break;
//                }
//                // Second case is image on back side, solid colored front.
//                case 1: {
//                    Bitmap back = loadBitmap(width, height, 2);
//                    page.setTexture(back, CurlPage.SIDE_BACK);
//                    page.setColor(Color.rgb(127, 140, 180), CurlPage.SIDE_FRONT);
//                    break;
//                }
//                // Third case is images on both sides.
//                case 2: {
//                    Bitmap front = loadBitmap(width, height, 1);
//                    Bitmap back = loadBitmap(width, height, 3);
//                    page.setTexture(front, CurlPage.SIDE_FRONT);
//                    page.setTexture(back, CurlPage.SIDE_BACK);
//                    break;
//                }
//                // Fourth case is images on both sides - plus they are blend against
//                // separate colors.
//                case 3: {
//                    Bitmap front = loadBitmap(width, height, 2);
//                    Bitmap back = loadBitmap(width, height, 1);
//                    page.setTexture(front, CurlPage.SIDE_FRONT);
//                    page.setTexture(back, CurlPage.SIDE_BACK);
//                    page.setColor(Color.argb(127, 170, 130, 255),
//                            CurlPage.SIDE_FRONT);
//                    page.setColor(Color.rgb(255, 190, 150), CurlPage.SIDE_BACK);
//                    break;
//                }
//                // Fifth case is same image is assigned to front and back. In this
//                // scenario only one texture is used and shared for both sides.
//                case 4:
//                    Bitmap front = loadBitmap(width, height, 0);
//                    page.setTexture(front, CurlPage.SIDE_BOTH);
//                    page.setColor(Color.argb(127, 255, 255, 255),
//                            CurlPage.SIDE_BACK);
//                    break;
//            }
        }

    }

    private class SizeChangedObserver implements CurlView.SizeChangedObserver {
        @Override
        public void onSizeChanged(int width, int height) {
            if (mRecipePageList == null) {
                mRecipePageList = _getRecipePageList(width, height);
                mShowTimerIcon = true;
                mCurrentTimerValues = mRecipePageList.get(0).getTimerValues();
            }

            if (width > height) {
                mCurlView.setViewMode(CurlView.SHOW_TWO_PAGES);
                mCurlView.setMargins(0, 0, 0, 0);
            } else {
                mCurlView.setViewMode(CurlView.SHOW_ONE_PAGE);
                mCurlView.setMargins(0, 0, 0, 0);
            }
        }
    }

    @Override
    public Object onRetainNonConfigurationInstance() {
        return mCurlView.getCurrentIndex();
    }
}