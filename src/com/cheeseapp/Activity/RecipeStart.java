package com.cheeseapp.Activity;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.*;
import com.cheeseapp.DbAdapter.CheeseDbAdapter;
import com.cheeseapp.DbAdapter.IngredientDbAdapter;
import com.cheeseapp.DbAdapter.RecipeDbAdapter;
import com.cheeseapp.R;
import com.cheeseapp.Util.Util;

import java.util.ArrayList;
import java.util.HashMap;

public class RecipeStart extends MyCheeseActivity {

    private IngredientDbAdapter mIngredientDb;
    private RecipeDbAdapter mRecipeDb;
    private Cursor mRecipeCursor;
    private CheeseDbAdapter mCheeseDb;
    private Cursor mCheeseCursor;

    private long mCheeseId;
    private String mCheeseName;
    private int mCheeseImgResource;
    private Double mOriginalYield;
    private Double mYield;
    private String mTime;
    private ArrayList<HashMap> mIngredients = new ArrayList<HashMap>();
    private ArrayList<HashMap> mOriginalIngredients = new ArrayList<HashMap>();

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        _initializeDatabases();
        setContentView(R.layout.recipe_start);

        mCheeseId = _getCheeseId(savedInstanceState);
        mCheeseName = _getCheeseName(savedInstanceState);
        mCheeseImgResource = _getCheeseImageResource(savedInstanceState);
        mYield = _getCheeseYield(savedInstanceState);
        mIngredients = _getIngredients(savedInstanceState);
        mTime = _getTime(savedInstanceState);

        //Cheese picture
        ImageView cheeseImg = (ImageView) findViewById(R.id.recipeCheeseImg);
        cheeseImg.setImageResource(mCheeseImgResource);

        //Cheese name
        TextView cheeseNameView = (TextView) findViewById(R.id.recipeCheeseName);
        cheeseNameView.setText(mCheeseName);

        //Recipe time
        TextView timeView = (TextView) findViewById(R.id.timeText);
        timeView.setText(mTime + " hours");

        //Yield
        _setupYieldSpinner();

        //Start Button
        _setupStartButton();

        //Recipe ingredients
        _setupRecipeIngredients();
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
        super.onDestroy();
        mRecipeDb.close();
        mIngredientDb.close();
        mCheeseDb.close();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putDouble("yield", mYield);
        outState.putSerializable("ingredients", mIngredients);
        outState.putString("time", mTime);
        outState.putString("cheese_name", mCheeseName);
        outState.putInt("cheese_img_resource", mCheeseImgResource);
    }

    private void _setupStartButton() {
        Button startButton = (Button) findViewById(R.id.recipeStartButton);
        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), Recipe.class);
                intent.putExtra(CheeseInfo.CHEESE_ID_KEY, mCheeseId);

                startActivity(intent);
                overridePendingTransition(R.anim.fadein, R.anim.fadeout);
            }
        });
    }

    private ArrayList<HashMap> _getIngredients(Bundle savedInstanceState) {
        @SuppressWarnings("unchecked")
        ArrayList<HashMap> ingredients = (savedInstanceState == null) ? null :  (ArrayList<HashMap>) savedInstanceState.getSerializable("ingredients");

        if (ingredients == null) {
            ingredients = new ArrayList<HashMap>();
            long recipeId = _getRecipeId(savedInstanceState);
            Cursor ingredientCursor = mIngredientDb.getIngredientsForRecipe(recipeId);
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

    private void _setupYieldSpinner() {
        Spinner YieldSpinner = (Spinner) findViewById(R.id.recipeYieldSpinner);
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

                _recalculateIngredientQuantities();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    private void _recalculateIngredientQuantities() {

        for (int i=0; i < mOriginalIngredients.size(); i++) {
            @SuppressWarnings("unchecked")
            HashMap<String, String> ingredient = (HashMap<String, String>) mOriginalIngredients.get(i);

            Double quantity = Double.parseDouble(ingredient.get("quantity"));
            String newQuantity = String.valueOf((mYield / mOriginalYield) * quantity);

            @SuppressWarnings("unchecked")
            HashMap<String, String> updatedIngredient = mIngredients.get(i);
            updatedIngredient.put("quantity", newQuantity);
        }

        _setupRecipeIngredients();
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

    private void _setupRecipeIngredients() {
        LinearLayout ingredientList = (LinearLayout) findViewById(R.id.mainIngredientList);

        LinearLayout warningTextLayout = (LinearLayout) findViewById(R.id.recipeWarningTextLayout);
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

    private String _getTime(Bundle savedInstanceState) {
        String time = (savedInstanceState == null) ? null :  (String) savedInstanceState.getSerializable("time");

        if (time == null) {
            Cursor RecipeCursor = _getRecipeCursor();
            time = RecipeCursor.getString(RecipeCursor.getColumnIndexOrThrow(RecipeDbAdapter.KEY_TIME));
        }

        return time;
    }

    private Cursor _getRecipeCursor() {
        if (mRecipeCursor == null) {
            mRecipeCursor = mRecipeDb.getRecipeForCheese(mCheeseId);
        }

        return mRecipeCursor;
    }

    private long _getRecipeId(Bundle savedInstanceState) {
        Long recipeId = (savedInstanceState == null) ? null :  (Long) savedInstanceState.getSerializable("recipe_id");

        if (recipeId == null) {
            Cursor RecipeCursor = _getRecipeCursor();
            recipeId = RecipeCursor.getLong(RecipeCursor.getColumnIndexOrThrow(RecipeDbAdapter.KEY_ID));
        }

        return recipeId;
    }

    private long _getCheeseId(Bundle savedInstanceState) {
        Long cheeseId = (savedInstanceState == null) ? null : (Long) savedInstanceState.getSerializable(CheeseInfo.CHEESE_ID_KEY);

        if (cheeseId == null) {
            Bundle extras = getIntent().getExtras();
            cheeseId = extras.getLong(CheeseInfo.CHEESE_ID_KEY);
        }

        return cheeseId;
    }

    private String _getCheeseName(Bundle savedInstanceState) {
        String cheeseName = (savedInstanceState == null) ? null :  (String) savedInstanceState.getSerializable("cheese_name");

        if (cheeseName == null) {
            Cursor CheeseCursor = _getCheeseCursor();
            cheeseName = CheeseCursor.getString(CheeseCursor.getColumnIndexOrThrow(CheeseDbAdapter.KEY_NAME));
        }

        return cheeseName;
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

}