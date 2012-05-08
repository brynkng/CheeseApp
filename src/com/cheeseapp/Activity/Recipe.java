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

import java.util.ArrayList;

/**
 * User: Bryan King
 * Date: 5/6/12
 */
public class Recipe extends Activity {

    private long mCheeseId;
    private CheeseDbAdapter mCheeseDb;
    private RecipeDbAdapter mRecipeDb;
    private IngredientDbAdapter mIngredientDb;

    private ViewFlipper mFlipper;
    private long mRecipeId;
    private PopupWindow mPopup;
    private Double mYield;
    private ArrayList mRecipeViewList;
    private int mCheeseImgResource;
    private String mCheeseName;
    private String mTime;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        _initializeDatabases();
        mRecipeDb.prePopulate();
        mIngredientDb.prePopulate();

        mCheeseId = _getCheeseId(savedInstanceState);
        Cursor CheeseCursor = mCheeseDb.getCheese(mCheeseId);
        mCheeseImgResource = _getCheeseImageResource(savedInstanceState, CheeseCursor);
        mCheeseName = _getCheeseName(savedInstanceState, CheeseCursor);

        Cursor RecipeCursor = mRecipeDb.getRecipeForCheese(mCheeseId);
        mRecipeId = _getRecipeId(savedInstanceState, RecipeCursor);
        mTime = _getTime(savedInstanceState, RecipeCursor);
        mYield = _getCheeseYield(savedInstanceState, RecipeCursor);
    }

    private String _getTime(Bundle savedInstanceState, Cursor RecipeCursor) {
        String time = (savedInstanceState == null) ? null :  (String) savedInstanceState.getSerializable("cheese_name");

        if (time == null) {
            time = RecipeCursor.getString(RecipeCursor.getColumnIndexOrThrow(RecipeDbAdapter.KEY_TIME));
        }

        return time;
    }

    private String _getCheeseName(Bundle savedInstanceState, Cursor CheeseCursor) {
        String cheeseName = (savedInstanceState == null) ? null :  (String) savedInstanceState.getSerializable("cheese_name");
        
        if (cheeseName == null) {
            cheeseName = CheeseCursor.getString(CheeseCursor.getColumnIndexOrThrow(CheeseDbAdapter.KEY_NAME));
        }

        return cheeseName;
    }

    private long _getRecipeId(Bundle savedInstanceState, Cursor RecipeCursor) {
        Long recipeId = (savedInstanceState == null) ? null :  (Long) savedInstanceState.getSerializable("recipe_id");
        
        if (recipeId == null) {
            recipeId = RecipeCursor.getLong(RecipeCursor.getColumnIndexOrThrow(RecipeDbAdapter.KEY_ID));
        }

        return recipeId;
    }

    private int _getCheeseImageResource(Bundle savedInstanceState, Cursor CheeseCursor) {
        Integer cheeseImgResource = (savedInstanceState == null) ? null :  (Integer) savedInstanceState.getSerializable("cheese_img_resource");

        if (cheeseImgResource == null) {
            cheeseImgResource = Util.getImageResourceFromCursor(this, CheeseCursor, 1);
        }
        
        return cheeseImgResource;
    }

    @Override
    protected void onResume() {
        super.onResume();
        
        if (mFlipper == null) {
            _initializeFlipper();
        }
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
    }

    private void _initializeFlipper() {
        mFlipper = new ViewFlipper(this);
        View recipeLayoutView = _getHomeRecipeLayoutView();
        
        mFlipper.addView(recipeLayoutView);

        setContentView(mFlipper);
    }

    private View _getHomeRecipeLayoutView() {
        LayoutInflater inflater = (LayoutInflater)this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View recipeLayout = inflater.inflate(R.layout.recipe, (ViewGroup) findViewById(R.id.recipeLayout));

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
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        //Recipe ingredients
        LinearLayout ingredientList = (LinearLayout) recipeLayout.findViewById(R.id.mainIngredientList);
        Cursor ingredientCursor = mIngredientDb.getIngredientsForRecipe(mRecipeId);
        ingredientCursor.moveToFirst();
        while (!ingredientCursor.isAfterLast()) {
            TextView ingredientView = new TextView(this);
            String ingredientName = ingredientCursor.getString(ingredientCursor.getColumnIndexOrThrow(IngredientDbAdapter.KEY_NAME));
            String quantity = ingredientCursor.getString(ingredientCursor.getColumnIndexOrThrow(IngredientDbAdapter.LINKER_KEY_QUANTITY));
            String measurementName = ingredientCursor.getString(ingredientCursor.getColumnIndexOrThrow(IngredientDbAdapter.KEY_MEASUREMENT_NAME));
            ingredientView.setText("• " + quantity + " " + measurementName + " " + ingredientName);
            ingredientView.setTextColor(Color.BLACK);
            ingredientView.setTextSize(18);
            ingredientList.addView(ingredientView);

            ingredientCursor.moveToNext();
        }

        return recipeLayout;
    }

    private Double _getCheeseYield(Bundle savedInstanceState, Cursor RecipeCursor) {
        Double yield = (savedInstanceState == null) ? null :  (Double) savedInstanceState.getSerializable("yield");
        
        if (yield == null) {
            String yieldDouble = RecipeCursor.getString(RecipeCursor.getColumnIndexOrThrow(RecipeDbAdapter.KEY_YIELD));
            yield = Double.parseDouble(yieldDouble.substring(0, 1));
        }
        return yield;
    }

//    private void _initFlipper() {
//        mFlipper = new ViewFlipper(this);
//        View currentView = createView(listPosition,null);
//        if (currentView==null) {
//            // failed to create the view
//            finish();
//            return;
//        }
//        mFlipper.addView(currentView);
//
//        if (listPosition>0) { // is there a previous?
//            View prevView = createView(listPosition-1,null);
//            mFlipper.addView(prevView,0);
//            mFlipper.showNext();
//        }
//
//        // are we starting at the end and we have more than 2 entries?
//        if (((listPosition+1)==rowids.length) && (rowids.length > 2)) {
//            View prevView = createView(listPosition-2,null);
//            mFlipper.addView(prevView,0);
//            mFlipper.showNext();
//        }
//
//        if (rowids.length > (listPosition+1)) {  // is there a next?
//            View nextView = createView(listPosition+1,null);
//            mFlipper.addView(nextView, mFlipper.getChildCount());
//        }
//
//        // are we starting at the start and we have more than 2 entries?
//        if ((listPosition==0) && (rowids.length > 2)) {
//            mFlipper.addView(nextView, mFlipper.getChildCount());
//        }
//
//        setContentView(mFlipper);
//    }

//    private View createView(int position, View view) {
//        if (view==null) {
//            LayoutInflater inflater = (LayoutInflater)this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//            view = inflater.inflate(R.layout.recipe, null);
//        }
//        if (_populateFields(rowids[position], view) == false) {
//            // failed to retreive record
//            return null;
//        }
//        Button previousButton = (Button) view.findViewById(R.id.prev_pass);
//        if (position>0) {    // is there a previous?
//            previousButton.setEnabled(true);
//            previousButton.setOnClickListener(new prevButtonListener());
//        } else {
//            previousButton.setEnabled(false);
//        }
//        Button nextButton = (Button) view.findViewById(R.id.next_pass);
//        if (rowids.length > (position+1)) {  // is there a next?
//            nextButton.setEnabled(true);
//            nextButton.setOnClickListener(new nextButtonListener());
//        } else {
//            nextButton.setEnabled(false);
//        }
//
//        Button goButton = (Button) view.findViewById(R.id.go);
//        goButton.setOnClickListener(new goButtonListener());
//
//        TextView usernameText = (TextView) view.findViewById(R.id.username);
//        usernameText.setOnClickListener(new usernameTextListener());
//        TextView passwordText = (TextView) view.findViewById(R.id.password);
//        passwordText.setOnClickListener(new passwordTextListener());
//        return view;
//    }

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

//    private void _setupCheesePicture(Cursor mCheeseCursor) {
//        int firstColumn = 1;
//        int picResource = Util.getImageResourceFromCursor(this, mCheeseCursor, firstColumn);
//
//        ImageView imageView = (ImageView) findViewById(R.id.largeRecipeCheeseImg);
//        imageView.setImageResource(picResource);
//    }

//    private void _setupCheeseName(Cursor mCheeseCursor) {
//        TextView CheeseName = (TextView) findViewById(R.id.cheeseRecipeName);
//        CheeseName.setText(mCheeseCursor.getString(mCheeseCursor.getColumnIndexOrThrow(CheeseDbAdapter.KEY_NAME)));
//    }

    private long _getCheeseId(Bundle savedInstanceState) {
        Long cheeseId = (savedInstanceState == null) ? null : (Long) savedInstanceState.getSerializable(CheeseInfo.CHEESE_ID_KEY);

        if (cheeseId == null) {
            Bundle extras = getIntent().getExtras();
            cheeseId = extras.getLong(CheeseInfo.CHEESE_ID_KEY);
        }

        return cheeseId;
    }

}