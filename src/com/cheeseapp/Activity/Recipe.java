package com.cheeseapp.Activity;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.*;
import android.widget.*;
import com.cheeseapp.CategoryGroupedDirection;
import com.cheeseapp.Curl.CurlPage;
import com.cheeseapp.Curl.CurlView;
import com.cheeseapp.DbAdapter.*;
import com.cheeseapp.R;
import com.cheeseapp.Util.Util;

import java.util.*;

import static java.lang.Math.abs;

/**
 * User: Bryan King
 * Date: 5/6/12
 */
public class Recipe extends MyCheeseActivity {

    private long mCheeseId;
    private RecipeDbAdapter mRecipeDb;
    private DirectionDbAdapter mDirectionDb;
    private Cursor mRecipeCursor;
    private Cursor mDirectionCursor;

    private long mRecipeId;
    private ArrayList<View> mRecipeViewList;
    private ArrayList<CategoryGroupedDirection> mCategoryGroupedDirections;
    private CurlView mCurlView;
    private DirectionCategoryDbAdapter mDirectionCategoryDb;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        _initializeDatabases();
        setContentView(R.layout.recipe);

        int index = 0;
        if (getLastNonConfigurationInstance() != null) {
            index = (Integer) getLastNonConfigurationInstance();
        }

        mCheeseId = _getCheeseId(savedInstanceState);

        mRecipeId = _getRecipeId(savedInstanceState);


        mCategoryGroupedDirections = _getCategoryGroupedDirections(savedInstanceState);

        mCurlView = (CurlView) findViewById(R.id.recipeCurlView);
        mCurlView.setCurrentIndex(index);
        mCurlView.setPageProvider(new PageProvider());
        mCurlView.setSizeChangedObserver(new SizeChangedObserver());
        mCurlView.setBackgroundColor(Color.WHITE);
        mCurlView.setAllowLastPageCurl(false);
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

    }

    private ArrayList<View> _getRecipeViewList(int width, int height) {
        ArrayList<View> recipeViewList = new ArrayList<View>();

        LayoutInflater inflater = (LayoutInflater)this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View emptyRecipeLayout = inflater.inflate(R.layout.recipe_page, (ViewGroup) findViewById(R.id.recipePageLayout));
        TextView emptyRecipeText = (TextView) emptyRecipeLayout.findViewById(R.id.recipePageDirectionsText);
        TextView emptyCategoryText = (TextView) emptyRecipeLayout.findViewById(R.id.recipeDirectionCategory);
        TextView emptyIngredientsText = (TextView) emptyRecipeLayout.findViewById(R.id.recipeDirectionIngredients);

        Paint recipeTextPaint = emptyRecipeText.getPaint();
        Paint categoryTextPaint = emptyCategoryText.getPaint();
        Paint ingredientTextPaint = emptyIngredientsText.getPaint();
        float fontHeight = abs(recipeTextPaint.getFontMetrics().top) + abs(recipeTextPaint.getFontMetrics().bottom);
        float headerHeight = abs(categoryTextPaint.getFontMetrics().top) + abs(categoryTextPaint.getFontMetrics().bottom);
        headerHeight += abs(ingredientTextPaint.getFontMetrics().top) + abs(ingredientTextPaint.getFontMetrics().bottom);
        headerHeight *= 2;
        int maxLines = (int) abs((height - headerHeight) / fontHeight);
        int maxNumCharsPerLine = (int) (width / (recipeTextPaint.getTextSize() / 2));

        for (CategoryGroupedDirection RecipeDirection : mCategoryGroupedDirections) {
            String directions = RecipeDirection.getDirections();
            String categoryName = mDirectionCategoryDb.getDirectionCategoryName(RecipeDirection.getDirectionCategoryId());


            while (directions.length() > 0) {
                View recipeLayout = inflater.inflate(R.layout.recipe_page, (ViewGroup) findViewById(R.id.recipePageLayout));

                TextView directionCategoryText = (TextView) recipeLayout.findViewById(R.id.recipeDirectionCategory);
                directionCategoryText.setText(categoryName);
                TextView directionIngredientsText = (TextView) recipeLayout.findViewById(R.id.recipeDirectionIngredients);
                directionIngredientsText.setText("2 Gallons Whole Milk      1 Packet Meso Starter");

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
                }

                recipeText.setText(finalPageText);
                recipeViewList.add(recipeLayout);
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

        mDirectionCategoryDb = new DirectionCategoryDbAdapter(this);
        mDirectionCategoryDb.open();
    }

    @Override
    protected void onDestroy() {
        mRecipeDb.close();
        mDirectionDb.close();
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
            return mRecipeViewList.size();
        }

        private Bitmap loadBitmap(int width, int height, int index) {
            Bitmap b = Bitmap.createBitmap(width, height,
                    Bitmap.Config.ARGB_8888);
            Canvas c = new Canvas(b);
            LinearLayout recipeView = (LinearLayout) mRecipeViewList.get(index);
            recipeView.measure(
                    View.MeasureSpec.makeMeasureSpec(width, View.MeasureSpec.EXACTLY),
                    View.MeasureSpec.makeMeasureSpec(height, View.MeasureSpec.EXACTLY));
            recipeView.layout(0, 0, recipeView.getMeasuredWidth(), recipeView.getMeasuredHeight());

            recipeView.draw(c);
            return b;
        }

        @Override
        public void updatePage(CurlPage page, int width, int height, int index) {

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
            if (mRecipeViewList == null) {
                mRecipeViewList = _getRecipeViewList(width, height);
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