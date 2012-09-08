package com.cheeseapp;

public class CategoryGroupedDirection {
    int mDirectionCategoryId;
    String mDirections;

    public CategoryGroupedDirection(int directionCategoryId, String directions) {
        mDirectionCategoryId = directionCategoryId;
        mDirections = directions;
    }

    public int getDirectionCategoryId() {
        return mDirectionCategoryId;
    }

    public String getDirections() {
        return mDirections;
    }
}
