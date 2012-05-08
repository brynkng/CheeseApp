package com.cheeseapp.Activity;

import android.app.Activity;
import android.content.Context;
import android.content.res.ColorStateList;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import com.cheeseapp.DbAdapter.*;
import com.cheeseapp.R;
import com.cheeseapp.Util.Util;

import java.util.ArrayList;
import java.util.zip.Inflater;

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
    private long recipeId;
    private Cursor recipeCursor;
    private Cursor cheeseCursor;
    private ArrayList mRecipeViewList;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        _initializeDatabases();
        mRecipeDb.prePopulate();
        mIngredientDb.prePopulate();


        mCheeseId = _getCheeseId(savedInstanceState);
        cheeseCursor =  mCheeseDb.getCheese(mCheeseId);
        startManagingCursor(cheeseCursor);

        recipeCursor = mRecipeDb.getRecipeForCheese(mCheeseId);
        startManagingCursor(recipeCursor);
        recipeCursor.moveToFirst();
        recipeId = recipeCursor.getLong(recipeCursor.getColumnIndexOrThrow(RecipeDbAdapter.KEY_ID));

//        _setupCheesePicture(CheeseCursor);
//
//        _setupCheeseName(CheeseCursor);
    }

    @Override
    protected void onResume() {
        super.onResume();
        
        if (mFlipper == null) {
            _initializeFlipper();
        }
    }

    private void _initializeFlipper() {
        mFlipper = new ViewFlipper(this);
        LayoutInflater inflater = (LayoutInflater)this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View recipeLayout = inflater.inflate(R.layout.recipe, (ViewGroup) findViewById(R.id.recipeHome));

        //Cheese picture
        ImageView cheeseImg = (ImageView) recipeLayout.findViewById(R.id.recipeCheeseImg);
        cheeseImg.setImageResource(Util.getImageResourceFromCursor(this, cheeseCursor, 1));
        
        //Cheese name
        TextView cheeseNameView = (TextView) recipeLayout.findViewById(R.id.recipeCheeseName);
        String cheeseName = cheeseCursor.getString(cheeseCursor.getColumnIndexOrThrow(CheeseDbAdapter.KEY_NAME));
        cheeseNameView.setText(cheeseName);
        
        //Recipe time
        TextView timeView = (TextView) recipeLayout.findViewById(R.id.timeText);
        String time = recipeCursor.getString(recipeCursor.getColumnIndexOrThrow(RecipeDbAdapter.KEY_TIME));
        timeView.setText(time + " hours");

        //Recipe yield
        TextView yieldView = (TextView) recipeLayout.findViewById(R.id.yieldText);
        String yield = recipeCursor.getString(recipeCursor.getColumnIndexOrThrow(RecipeDbAdapter.KEY_YIELD));
        yieldView.setText(yield + " pounds");

        //Recipe ingredients
        LinearLayout ingredientList = (LinearLayout) recipeLayout.findViewById(R.id.mainIngredientList);
        Cursor ingredientCursor = mIngredientDb.getIngredientsForRecipe(recipeId);
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

        //Change yield button
        Button changeYieldButton = (Button) recipeLayout.findViewById(R.id.changeYieldButton);
        changeYieldButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        
        mFlipper.addView(recipeLayout);

        setContentView(mFlipper);
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

//    private void _setupCheesePicture(Cursor cheeseCursor) {
//        int firstColumn = 1;
//        int picResource = Util.getImageResourceFromCursor(this, cheeseCursor, firstColumn);
//
//        ImageView imageView = (ImageView) findViewById(R.id.largeRecipeCheeseImg);
//        imageView.setImageResource(picResource);
//    }

//    private void _setupCheeseName(Cursor cheeseCursor) {
//        TextView CheeseName = (TextView) findViewById(R.id.cheeseRecipeName);
//        CheeseName.setText(cheeseCursor.getString(cheeseCursor.getColumnIndexOrThrow(CheeseDbAdapter.KEY_NAME)));
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