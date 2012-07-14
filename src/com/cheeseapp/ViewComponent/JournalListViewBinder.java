package com.cheeseapp.ViewComponent;

import android.database.Cursor;
import android.view.View;
import android.widget.TextView;
import com.cheeseapp.R;

/**
 * User: Bryan King
 * Date: 7/11/12
 */
public class JournalListViewBinder extends CheeseRowViewBinder {

    @Override
    protected int _getPictureResource() {
        return R.id.journalSmallCheeseImg;
    }

    @Override
    protected int _getRowNameResource() {
        return R.id.journalRowName;
    }

    @Override
    public boolean setViewValue(View view, Cursor cursor, int columnIndex) {
        super.setViewValue(view, cursor, columnIndex);
        int viewId = view.getId();
        if (viewId == R.id.journalCurrentDirectionCategory) {
            String categoryName = cursor.getString(columnIndex);

            TextView currentDirectionCategoryView = (TextView) view;
            currentDirectionCategoryView.setText(categoryName);

        } else if (viewId == R.id.journalLatestDate) {
            String journalDate = cursor.getString(columnIndex);

            TextView journalDateView = (TextView) view;
            journalDateView.setText(journalDate);
        }

        return true;
    }
}
