package com.cheeseapp.View;

import android.content.Context;
import android.view.Display;
import android.view.WindowManager;
import android.widget.TextView;

/**
 * User: Bryan King
 * Date: 5/12/12
 */
public class RecipeTextView extends TextView{

    private int mLineHeight;
    private int mLines;

    public RecipeTextView(Context context) {
        super(context);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mLines = getLineCount();
        mLineHeight = getLineHeight();
    }

//    public boolean isTooLarge(String text) {
//        WindowManager wm = (WindowManager) this.getContext().getSystemService(Context.WINDOW_SERVICE);
//        Display display = wm.getDefaultDisplay();
//        float height = display.getHeight();
//        float textAreaHeight = height - 200;
//        int maxLines = (int) Math.floor(textAreaHeight / mLineHeight);
//
//    }
}
