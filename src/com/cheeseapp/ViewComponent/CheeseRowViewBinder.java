package com.cheeseapp.ViewComponent;

import android.content.Context;
import android.database.Cursor;
import android.view.View;
import android.widget.ImageView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import com.cheeseapp.Util.Util;

/**
 * User: Bryan King
 * Date: 7/11/12
 */
public abstract class CheeseRowViewBinder implements SimpleCursorAdapter.ViewBinder {

    @Override
    public boolean setViewValue(View view, Cursor cursor, int columnIndex) {
        int viewId = view.getId();
        if (viewId == _getRowNameResource()) {
                TextView cheeseNameView = (TextView) view;
                cheeseNameView.setText(cursor.getString(columnIndex));
        } else if (viewId == _getPictureResource()) {
                ImageView cheesePictureView = (ImageView) view;
                Context context = view.getContext();

                int cheesePicture = Util.getImageResourceFromCursor(context, cursor, columnIndex);

                cheesePictureView.setImageResource(cheesePicture);
        }

        return true;
    }

    protected abstract int _getPictureResource();

    protected abstract int _getRowNameResource();
}
