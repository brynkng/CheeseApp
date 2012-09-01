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
import com.actionbarsherlock.app.SherlockActivity;
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
    private CheeseDbAdapter mCheeseDb;
    private RecipeDbAdapter mRecipeDb;
    private IngredientDbAdapter mIngredientDb;
    private DirectionDbAdapter mDirectionDb;
    private Cursor mRecipeCursor;
    private Cursor mDirectionCursor;

    private Cursor mCheeseCursor;
    private long mRecipeId;
    private Double mOriginalYield;
    private Double mYield;
    private ArrayList<View> mRecipeViewList;
    private ArrayList<HashMap> mRecipeDirections;
    private int mCheeseImgResource;
    private String mCheeseName;
    private String mTime;
    private ArrayList<HashMap> mIngredients = new ArrayList<HashMap>();
    private ArrayList<HashMap> mOriginalIngredients = new ArrayList<HashMap>();
    private CurlView mCurlView;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        _initializeDatabases();
        setContentView(R.layout.recipe);

        int index = 0;
        if (getLastNonConfigurationInstance() != null) {
            index = (Integer) getLastNonConfigurationInstance();
        }

        mCheeseId = _getCheeseId(savedInstanceState);
        mCheeseImgResource = _getCheeseImageResource(savedInstanceState);
        mCheeseName = _getCheeseName(savedInstanceState);

        mRecipeId = _getRecipeId(savedInstanceState);
        mTime = _getTime(savedInstanceState);
        mYield = _getCheeseYield(savedInstanceState);
        mIngredients = _getIngredients(savedInstanceState);

        mRecipeDirections = _getRecipeDirections(savedInstanceState);
        mRecipeViewList = _getRecipeViewList();

        mCurlView = (CurlView) findViewById(R.id.recipeCurlView);
        mCurlView.setPageProvider(new PageProvider());
        mCurlView.setSizeChangedObserver(new SizeChangedObserver());
        mCurlView.setCurrentIndex(index);
        mCurlView.setBackgroundColor(Color.WHITE);
    }

    private ArrayList<HashMap> _getRecipeDirections(Bundle savedInstanceState) {
        @SuppressWarnings("unchecked")
        ArrayList<HashMap> recipeDirections = (savedInstanceState == null) ? null :  (ArrayList<HashMap>) savedInstanceState.getSerializable("paged_directions");

        if (recipeDirections == null) {
            recipeDirections = new ArrayList<HashMap>();
            Cursor directionC = _getDirectionCursor();

            while (!directionC.isAfterLast()) {
                HashMap<Integer, String> direction = new HashMap<Integer, String>();
                Integer directionCategoryId = directionC.getInt(directionC.getColumnIndexOrThrow(DirectionDbAdapter.KEY_DIRECTION_CATEGORY_ID));
                String directionText = directionC.getString(directionC.getColumnIndexOrThrow(DirectionDbAdapter.KEY_DIRECTION));
                direction.put(directionCategoryId, directionText);

                recipeDirections.add(direction);
                directionC.moveToNext();
            }

        }

        return recipeDirections;
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

    private String _getTime(Bundle savedInstanceState) {
        String time = (savedInstanceState == null) ? null :  (String) savedInstanceState.getSerializable("time");

        if (time == null) {
            Cursor RecipeCursor = _getRecipeCursor();
            time = RecipeCursor.getString(RecipeCursor.getColumnIndexOrThrow(RecipeDbAdapter.KEY_TIME));
        }

        return time;
    }

    private String _getCheeseName(Bundle savedInstanceState) {
        String cheeseName = (savedInstanceState == null) ? null :  (String) savedInstanceState.getSerializable("cheese_name");

        if (cheeseName == null) {
            Cursor CheeseCursor = _getCheeseCursor();
            cheeseName = CheeseCursor.getString(CheeseCursor.getColumnIndexOrThrow(CheeseDbAdapter.KEY_NAME));
        }

        return cheeseName;
    }

    private long _getRecipeId(Bundle savedInstanceState) {
        Long recipeId = (savedInstanceState == null) ? null :  (Long) savedInstanceState.getSerializable("recipe_id");

        if (recipeId == null) {
            Cursor RecipeCursor = _getRecipeCursor();
            recipeId = RecipeCursor.getLong(RecipeCursor.getColumnIndexOrThrow(RecipeDbAdapter.KEY_ID));
        }

        return recipeId;
    }

    private int _getCheeseImageResource(Bundle savedInstanceState) {
        Integer cheeseImgResource = (savedInstanceState == null) ? null :  (Integer) savedInstanceState.getSerializable("cheese_img_resource");

        if (cheeseImgResource == null) {
            Cursor CheeseCursor = _getCheeseCursor();
            cheeseImgResource = Util.getImageResourceFromCursor(this, CheeseCursor, 1);
        }

        return cheeseImgResource;
    }

    private Cursor _getCheeseCursor() {
        if (mCheeseCursor == null) {
            mCheeseCursor = mCheeseDb.getCheese(mCheeseId);
        }

        return mCheeseCursor;
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putLong("cheese_id", mCheeseId);
        outState.putLong("recipe_id", mRecipeId);
        outState.putString("cheese_name", mCheeseName);
        outState.putString("time", mTime);
        outState.putInt("cheese_img_resource", mCheeseImgResource);
        outState.putDouble("yield", mYield);
        outState.putSerializable("ingredients", mIngredients);
    }

    private ArrayList<View> _getRecipeViewList() {
        ArrayList<View> recipeViewList = new ArrayList<View>();

        String text = "This is a recipe! Hello Bean. This is a recipe! Hello Bean. This is a recipe! Hello Bean. " +
                "This is a recipe! Hello Bean. This is a recipe! Hello Bean. This is a recipe! Hello Bean. This is a recipe! Hello Bean. " +
                "   " +
                "This is a recipe! Hello Bean. This is a recipe! Hello Bean. This is a recipe! Hello Bean. This is a recipe! Hello Bean. This is a recipe! " +
                "Hello Bean. This is a recipe! Hello Bean. This is a recipe! Hello Bean. This is a recipe! Hello Bean. This is a recipe! Hello Bean. " +
                "This is a recipe! Hello Bean. This is a recipe! Hello Bean. This is a recipe! Hello Bean. This is a recipe! Hello Bean. This is a recipe! " +
                "This is a recipe! Hello Bean. This is a recipe! Hello Bean. This is a recipe! Hello Bean. This is a recipe! Hello Bean. This is a recipe! " +
                "This is a recipe! Hello Bean. This is a recipe! Hello Bean. This is a recipe! Hello Bean. This is a recipe! Hello Bean. This is a recipe! " +
                "This is a recipe! Hello Bean. This is a recipe! Hello Bean. This is a recipe! Hello Bean. This is a recipe! Hello Bean. This is a recipe! " +
                "This is a recipe! Hello Bean. This is a recipe! Hello Bean. This is a recipe! Hello Bean. This is a recipe! Hello Bean. This is a recipe! " +
                "This is a recipe! Hello Bean. This is a recipe! Hello Bean. This is a recipe! Hello Bean. This is a recipe! Hello Bean. This is a recipe! " +
                "This is a recipe! Hello Bean. This is a recipe! Hello Bean. This is a recipe! Hello Bean. This is a recipe! Hello Bean. This is a recipe! " +
                "This is a recipe! Hello Bean. This is a recipe! Hello Bean. This is a recipe! Hello Bean. This is a recipe! Hello Bean. This is a recipe! " +
                "This is a recipe! Hello Bean. This is a recipe! Hello Bean. This is a recipe! Hello Bean. This is a recipe! Hello Bean. This is a recipe! " +
                "This is a recipe! Hello Bean. This is a recipe! Hello Bean. This is a recipe! Hello Bean. This is a recipe! Hello Bean. This is a recipe! " +
                "This is a recipe! Hello Bean. This is a recipe! Hello Bean. This is a recipe! Hello Bean. This is a recipe! Hello Bean. This is a recipe! " +
                "This is a recipe! Hello Bean. This is a recipe! Hello Bean. This is a recipe! Hello Bean. This is a recipe! Hello Bean. This is a recipe! " +
                "Hello Bean. This is a recipe! Hello Bean. This is a recipe! Hello Bean. This is a recipe! Hello Bean. This is a recipe! Hello Bean. 3424234234234";

        LayoutInflater inflater = (LayoutInflater)this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View emptyRecipeLayout = inflater.inflate(R.layout.recipe_page, (ViewGroup) findViewById(R.id.recipePageLayout));
        TextView emptyRecipeText = (TextView) emptyRecipeLayout.findViewById(R.id.recipePageDirectionsText);
        TextView directionCategoryText = (TextView) emptyRecipeLayout.findViewById(R.id.recipeDirectionCategory);
        directionCategoryText.setText("Ripening");
        TextView directionIngredientsText = (TextView) emptyRecipeLayout.findViewById(R.id.recipeDirectionIngredients);
        directionIngredientsText.setText("2 Gallons Whole Milk      1 Packet Meso Starter");

        float width = 375;
        int height = 500;

        emptyRecipeText.setHeight(500);
        Paint textPaint = emptyRecipeText.getPaint();
        float fontHeight = abs(textPaint.getFontMetrics().top) + abs(textPaint.getFontMetrics().bottom);
        textPaint.getTextSize();
        int maxNumCharsPerLine = textPaint.breakText(text, true, width, null) - 1;
        int maxLines = (int) abs(height / fontHeight);

        while (text.length() > 0) {
            View recipeLayout = inflater.inflate(R.layout.recipe_page, (ViewGroup) findViewById(R.id.recipePageLayout));
            TextView recipeText = (TextView) recipeLayout.findViewById(R.id.recipePageDirectionsText);
            String finalText = "";

            int currentLineNum = 1;
            while (currentLineNum <= maxLines) {
                int numCharsOnLine = (maxNumCharsPerLine < text.length()) ? maxNumCharsPerLine : text.length();
                String subText = TextUtils.substring(text, 0, numCharsOnLine);
                subText += "\n";

                //if this is the last line, clear out the text
                //otherwise remove the portion we added
                if (numCharsOnLine == text.length()) {
                    text = "";
                } else {
                    text = TextUtils.substring(text, numCharsOnLine, text.length());
                }

                finalText += subText;

                currentLineNum++;
            }

            recipeText.setText(finalText);
            recipeViewList.add(recipeLayout);
        }

        return recipeViewList;
    }

    private View _getHomeRecipeLayoutView() {
        LayoutInflater inflater = (LayoutInflater)this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View recipeLayout = inflater.inflate(R.layout.recipe_home, (ViewGroup) findViewById(R.id.recipeScrollLayout));

        //Cheese picture
        ImageView cheeseImg = (ImageView) recipeLayout.findViewById(R.id.recipeCheeseImg);
        cheeseImg.setImageResource(mCheeseImgResource);

        //Cheese name
        TextView cheeseNameView = (TextView) recipeLayout.findViewById(R.id.recipeCheeseName);
        cheeseNameView.setText(mCheeseName);

        //Recipe time
        TextView timeView = (TextView) recipeLayout.findViewById(R.id.timeText);
        timeView.setText(mTime + " hours");

        //Yield
        _setupYieldSpinner(recipeLayout);

        //Recipe ingredients
        _setupRecipeIngredients(recipeLayout);

        return recipeLayout;
    }

    private ArrayList<HashMap> _getIngredients(Bundle savedInstanceState) {
        @SuppressWarnings("unchecked")
        ArrayList<HashMap> ingredients = (savedInstanceState == null) ? null :  (ArrayList<HashMap>) savedInstanceState.getSerializable("ingredients");
        
        if (ingredients == null) {
            ingredients = new ArrayList<HashMap>();
            Cursor ingredientCursor = mIngredientDb.getIngredientsForRecipe(mRecipeId);
            ingredientCursor.moveToFirst();
            while (!ingredientCursor.isAfterLast()) {
                String ingredientName = ingredientCursor.getString(ingredientCursor.getColumnIndexOrThrow(IngredientDbAdapter.KEY_NAME));
                String quantity = ingredientCursor.getString(ingredientCursor.getColumnIndexOrThrow(IngredientDbAdapter.LINKER_KEY_QUANTITY));
                String unit = ingredientCursor.getString(ingredientCursor.getColumnIndexOrThrow(IngredientDbAdapter.KEY_UNIT));

                HashMap<String, String> ingredientParts = new HashMap<String, String>();
                ingredientParts.put("name", ingredientName);
                ingredientParts.put("quantity", quantity);
                ingredientParts.put("unit", unit);

                ingredients.add(ingredientParts);

                ingredientCursor.moveToNext();
            }

            mOriginalIngredients = _getClonedArrayListHashMap(ingredients);
        }

        return ingredients;
    }

    private ArrayList<HashMap> _getClonedArrayListHashMap(ArrayList<HashMap> ingredients) {
        ArrayList<HashMap> clonedArrayList = new ArrayList<HashMap>();

        for (HashMap ingredient : ingredients) {
            HashMap<String, String> newIngredient = new HashMap<String, String>();
            for (Object o : ingredient.entrySet()) {
                @SuppressWarnings("unchecked")
                HashMap.Entry<String, String> ingredientPart = (HashMap.Entry<String, String>) o;
                newIngredient.put(ingredientPart.getKey(), ingredientPart.getValue());
            }
            
            clonedArrayList.add(newIngredient);
        }

        return clonedArrayList;
    }

    private void _setupRecipeIngredients(View recipeLayout) {
        LinearLayout ingredientList = (LinearLayout) recipeLayout.findViewById(R.id.mainIngredientList);

        LinearLayout warningTextLayout = (LinearLayout) recipeLayout.findViewById(R.id.recipeWarningTextLayout);
        if (!mYield.equals(mOriginalYield)) {
            warningTextLayout.removeAllViews();

            TextView warningText = new TextView(this);
            warningText.setTextColor(Color.parseColor("#ff0000"));
            warningText.setTextSize(16);
            warningText.setText("Warning - The following recipe is not guaranteed to work for the calculated ingredient quantities");

            warningTextLayout.addView(warningText);
        } else {
            warningTextLayout.removeAllViews();
        }

        if (ingredientList.getChildCount() > 0) {
            ingredientList.removeAllViews();
        }

        for (HashMap ingredient : mIngredients) {
            TextView ingredientView = new TextView(this);
            Double quantity = Double.parseDouble((String) ingredient.get("quantity"));
            String unit = (String) ingredient.get("unit");
            String name = (String) ingredient.get("name");

            if (quantity > 1) {
                unit = unit + "s";
            }

            ingredientView.setText("• " + quantity + " " + unit + " " + name);
            ingredientView.setTextColor(Color.BLACK);
            ingredientView.setTextSize(18);
            ingredientList.addView(ingredientView);
        }
    }

    private void _setupYieldSpinner(final View recipeLayout) {
        Spinner YieldSpinner = (Spinner) recipeLayout.findViewById(R.id.recipeYieldSpinner);
        ArrayAdapter<CharSequence> yieldSpinnerAdapter = ArrayAdapter.createFromResource(
                this,
                R.array.yield_amounts,
                android.R.layout.simple_spinner_dropdown_item
        );
        yieldSpinnerAdapter.setDropDownViewResource(R.layout.yield_spinner_layout);
        YieldSpinner.setAdapter(yieldSpinnerAdapter);

        int yieldPosition;
        if (mYield == .5) {
            yieldPosition = 0;
        } else {
            yieldPosition = (int) Math.round(mYield);
        }

        YieldSpinner.setSelection(yieldPosition);
        YieldSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) {
                    mYield = .5;
                } else {
                    mYield = Double.parseDouble(Integer.toString(position));
                }

                _recalculateIngredientQuantities(recipeLayout);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    private void _recalculateIngredientQuantities(View recipeLayout) {

        for (int i=0; i < mOriginalIngredients.size(); i++) {
            @SuppressWarnings("unchecked")
            HashMap<String, String> ingredient = (HashMap<String, String>) mOriginalIngredients.get(i);
            
            Double quantity = Double.parseDouble(ingredient.get("quantity"));
            String newQuantity = String.valueOf((mYield / mOriginalYield) * quantity);

            @SuppressWarnings("unchecked")
            HashMap<String, String> updatedIngredient = mIngredients.get(i);
            updatedIngredient.put("quantity", newQuantity);
        }
        
        _setupRecipeIngredients(recipeLayout);
    }

    private Double _getCheeseYield(Bundle savedInstanceState) {
        Double yield = (savedInstanceState == null) ? null :  (Double) savedInstanceState.getSerializable("yield");
        
        if (yield == null) {
            Cursor RecipeCursor = _getRecipeCursor();
            String yieldDouble = RecipeCursor.getString(RecipeCursor.getColumnIndexOrThrow(RecipeDbAdapter.KEY_YIELD));
            yield = Double.parseDouble(yieldDouble.substring(0, 1));
            mOriginalYield = yield;
        }
        return yield;
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
        mCheeseDb = new CheeseDbAdapter(this);
        mCheeseDb.open();

        mRecipeDb = new RecipeDbAdapter(this);
        mRecipeDb.open();

        mIngredientDb = new IngredientDbAdapter(this);
        mIngredientDb.open();

        mDirectionDb = new DirectionDbAdapter(this);
        mDirectionDb.open();
    }

    @Override
    protected void onDestroy() {
        mCheeseDb.close();
        mRecipeDb.close();
        mIngredientDb.close();
        mDirectionDb.close();

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

            switch (index) {
                // First case is image on front side, solid colored back.
                case 0: {
                    Bitmap front = loadBitmap(width, height, 0);
                    page.setTexture(front, CurlPage.SIDE_FRONT);
                    page.setColor(Color.rgb(180, 180, 180), CurlPage.SIDE_BACK);
                    break;
                }
                // Second case is image on back side, solid colored front.
                case 1: {
                    Bitmap back = loadBitmap(width, height, 2);
                    page.setTexture(back, CurlPage.SIDE_BACK);
                    page.setColor(Color.rgb(127, 140, 180), CurlPage.SIDE_FRONT);
                    break;
                }
                // Third case is images on both sides.
                case 2: {
                    Bitmap front = loadBitmap(width, height, 1);
                    Bitmap back = loadBitmap(width, height, 3);
                    page.setTexture(front, CurlPage.SIDE_FRONT);
                    page.setTexture(back, CurlPage.SIDE_BACK);
                    break;
                }
                // Fourth case is images on both sides - plus they are blend against
                // separate colors.
                case 3: {
                    Bitmap front = loadBitmap(width, height, 2);
                    Bitmap back = loadBitmap(width, height, 1);
                    page.setTexture(front, CurlPage.SIDE_FRONT);
                    page.setTexture(back, CurlPage.SIDE_BACK);
                    page.setColor(Color.argb(127, 170, 130, 255),
                            CurlPage.SIDE_FRONT);
                    page.setColor(Color.rgb(255, 190, 150), CurlPage.SIDE_BACK);
                    break;
                }
                // Fifth case is same image is assigned to front and back. In this
                // scenario only one texture is used and shared for both sides.
                case 4:
                    Bitmap front = loadBitmap(width, height, 0);
                    page.setTexture(front, CurlPage.SIDE_BOTH);
                    page.setColor(Color.argb(127, 255, 255, 255),
                            CurlPage.SIDE_BACK);
                    break;
            }
        }

    }

    private class SizeChangedObserver implements CurlView.SizeChangedObserver {
        @Override
        public void onSizeChanged(int w, int h) {
            if (w > h) {
                mCurlView.setViewMode(CurlView.SHOW_TWO_PAGES);
                mCurlView.setMargins(.01f, .05f, .01f, .05f);
            } else {
                mCurlView.setViewMode(CurlView.SHOW_ONE_PAGE);
                mCurlView.setMargins(.01f, .01f, .01f, .01f);
            }
        }
    }

    @Override
    public Object onRetainNonConfigurationInstance() {
        return mCurlView.getCurrentIndex();
    }
}