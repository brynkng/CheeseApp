package com.cheeseapp.Activity;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.view.*;
import android.widget.*;
import com.cheeseapp.DbAdapter.*;
import com.cheeseapp.R;
import com.cheeseapp.Util.Util;

import java.util.*;

/**
 * User: Bryan King
 * Date: 5/6/12
 */
public class Recipe extends Activity {

    private long mCheeseId;
    private CheeseDbAdapter mCheeseDb;
    private RecipeDbAdapter mRecipeDb;
    private IngredientDbAdapter mIngredientDb;
    private Cursor mRecipeCursor;
    private Cursor mCheeseCursor;

    private ViewFlipper mFlipper;
    private long mRecipeId;
    private Double mOriginalYield;
    private Double mYield;
    private ArrayList<View> mRecipeViewList;
    private int mCheeseImgResource;
    private String mCheeseName;
    private String mTime;
    private ArrayList<HashMap> mIngredients = new ArrayList<HashMap>();
    private ArrayList<HashMap> mOriginalIngredients = new ArrayList<HashMap>();
    private float lastX;
    private float lastY;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        _initializeDatabases();
        setContentView(R.layout.recipe);

        mRecipeDb.prePopulate();
        mIngredientDb.prePopulate();

        mCheeseId = _getCheeseId(savedInstanceState);
        mCheeseImgResource = _getCheeseImageResource(savedInstanceState);
        mCheeseName = _getCheeseName(savedInstanceState);

        mRecipeId = _getRecipeId(savedInstanceState);
        mTime = _getTime(savedInstanceState);
        mYield = _getCheeseYield(savedInstanceState);
        mIngredients = _getIngredients(savedInstanceState);

        mRecipeViewList = _getRecipeViewList();
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (mFlipper == null) {
            _initializeFlipper();
        }
    }

    private ArrayList<View> _getRecipeViewList() {
        ArrayList<View> recipeViewList = new ArrayList<View>();
        LinearLayout recipeViewLayout = new LinearLayout(this);
        TextView recipeText = new TextView(this);
        recipeText.setText("This is a recipe! Hello Bean.");
        recipeViewLayout.addView(recipeText);
        recipeViewList.add(recipeViewLayout);

        return recipeViewList;
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

    private void _initializeFlipper() {
        mFlipper = (ViewFlipper) findViewById(R.id.recipeViewFlipper);

        View recipeLayoutView = _getHomeRecipeLayoutView();

//        ScrollView scrollView = (ScrollView) recipeLayoutView.findViewById(R.id.recipeScrollLayout);
//        scrollView.setOnTouchListener(flipperViewsOnTouchListener);

        recipeLayoutView.setOnTouchListener(flipperViewsOnTouchListener);

        mFlipper.addView(recipeLayoutView);

        for (View recipeView : mRecipeViewList) {
            recipeView.setOnTouchListener(flipperViewsOnTouchListener);
            mFlipper.addView(recipeView);
        }

        mFlipper.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mFlipper.showNext();
            }
        });

    }

    /**
     * This bad boy makes the views flip side to side, or up and down when its a scroll view. It takes in the
     * x and y coordinates and determines if its going left or right, or up and down and acts accordingly.
     *
     * Returning false tells it to continue with the parent functions (like a scroll view going up and down), and
     * halts the remainder of the touch events.
     *
     * Returning true tells it we like what's happening and want to continue to intercept these touch events so we can
     * keep processing them.
     */
    public View.OnTouchListener flipperViewsOnTouchListener = new View.OnTouchListener() {

        @Override
        public boolean onTouch(View v, MotionEvent touchEvent) {

            switch (touchEvent.getAction()) {

                case MotionEvent.ACTION_DOWN:
                {
                    lastX = touchEvent.getX();
                    lastY = touchEvent.getY();
                    break;
                }

                case MotionEvent.ACTION_MOVE:
                {
                    float currentX = touchEvent.getX();
                    float currentY = touchEvent.getY();

                    float distanceX = currentX - lastX;
                    float distanceY = currentY - lastY;

                    if (Math.abs(distanceY) > Math.abs(distanceX)) {
                        return false;
                    } else {
                        if (currentX < lastX) {

                            //If on last page, don't move further right
                            if (mFlipper.getDisplayedChild() == mRecipeViewList.size()) {
                                break;
                            } else {
                                mFlipper.setInAnimation(v.getContext(), R.anim.in_from_right);
                                mFlipper.setOutAnimation(v.getContext(), R.anim.out_to_left);
                                mFlipper.showNext();
                            }
                        } else {
                            //If on start page don't move further left
                            if (mFlipper.getDisplayedChild() == 0) {
                                break;
                            } else {
                                mFlipper.setInAnimation(v.getContext(), R.anim.in_from_left);
                                mFlipper.setOutAnimation(v.getContext(), R.anim.out_to_right);
                                mFlipper.showPrevious();
                            }
                        }

                        return true;
                    }
                }
            }

            boolean onStartPage = mFlipper.getDisplayedChild() != 0;
            return onStartPage;
        }
    };

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
                android.R.layout.simple_spinner_item
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

    private void _initializeDatabases() {
        mCheeseDb = new CheeseDbAdapter(this);
        mCheeseDb.open();

        mRecipeDb = new RecipeDbAdapter(this);
        mRecipeDb.open();

        mIngredientDb = new IngredientDbAdapter(this);
        mIngredientDb.open();
    }

    @Override
    protected void onDestroy() {
        mCheeseDb.close();
        mRecipeDb.close();
        mIngredientDb.close();

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
}