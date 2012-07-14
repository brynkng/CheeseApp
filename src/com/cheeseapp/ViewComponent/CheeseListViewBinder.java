package com.cheeseapp.ViewComponent;

import android.database.Cursor;
import android.view.View;
import android.widget.ImageView;
import com.cheeseapp.DbAdapter.CheeseDbAdapter;
import com.cheeseapp.R;

/**
 * User: Bryan King
 * Date: 7/11/12
 */
public class CheeseListViewBinder extends CheeseRowViewBinder {
    @Override
    protected int _getPictureResource() {
        return R.id.cheeseRowName;
    }

    @Override
    protected int _getRowNameResource() {
        return R.id.cheeseListSmallCheeseImg;
    }

    @Override
    public boolean setViewValue(View view, Cursor cursor, int columnIndex) {
        super.setViewValue(view, cursor, columnIndex);
        int viewId = view.getId();
        if (viewId == R.id.listFavoriteIcon) {
            long cheeseId = cursor.getLong(columnIndex);
            CheeseDbAdapter cheeseDb = new CheeseDbAdapter(view.getContext());
            cheeseDb.open();
            final boolean isFavorite = cheeseDb.isFavorite(cheeseId);
            cheeseDb.close();

            if (isFavorite) {
                ImageView favoriteStarIcon = (ImageView) view;
                favoriteStarIcon.setImageResource(R.drawable.star_on);
            }
        }

        return true;
    }
}
