package com.cheeseapp;

import android.view.View;

import java.util.ArrayList;

public class RecipePage {
    private int directionCategoryId;
    private View recipeLayout;
    private ArrayList<TimerValue> timerValues;

    public RecipePage(int directionCategoryId, View recipeLayout, ArrayList<TimerValue> timerValues) {
        this.directionCategoryId = directionCategoryId;
        this.recipeLayout = recipeLayout;
        this.timerValues = timerValues;
    }

    public int getDirectionCategoryId() {
        return directionCategoryId;
    }

    public View getRecipeLayout() {
        return recipeLayout;
    }

    public ArrayList<TimerValue> getTimerValues() {
        return timerValues;
    }
}
